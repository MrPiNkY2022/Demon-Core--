// shamir.rs - Full Shamir Secret Sharing (GF(256) field) for mnemonic/seed splitting
// Threshold: t-of-n shares, constant-time friendly where possible
// References: Shamir 1979, SLIP-39, Bitcoin BIP-39 extensions

use anyhow::{anyhow, Result};
use rand::{rngs::OsRng, RngCore};
use sha3::{Digest, Keccak256};
use thiserror::Error;

#[derive(Error, Debug)]
pub enum ShamirError {
    #[error("Invalid threshold: {0}")]
    InvalidThreshold(String),
    #[error("Not enough shares: got {got}, need {need}")]
    NotEnoughShares { got: usize, need: usize },
    #[error("Share index out of range")]
    IndexOutOfRange,
    #[error("Polynomial evaluation error")]
    PolyEvalError,
    #[error("Lagrange interpolation failed")]
    InterpolationFailed,
}

const FIELD_PRIME: u8 = 257; // GF(257) for simplicity (prime field > 256)

/// Secret sharing parameters
pub struct ShamirParams {
    pub threshold: usize, // t
    pub shares: usize,    // n
}

impl ShamirParams {
    pub fn new(threshold: usize, shares: usize) -> Result<Self> {
        if threshold == 0 || threshold > shares {
            return Err(ShamirError::InvalidThreshold("threshold must be 1..=shares".to_string()).into());
        }
        Ok(Self { threshold, shares })
    }
}

/// Generate shares from secret (byte slice)
pub fn generate_shares(secret: &[u8], params: &ShamirParams) -> Result<Vec<Vec<u8>>> {
    if secret.is_empty() {
        return Err(anyhow!("Secret cannot be empty"));
    }

    let mut shares = Vec::with_capacity(params.shares);
    let mut rng = OsRng;

    // For each share index x = 1..n
    for x in 1..=params.shares {
        let mut share = vec![x as u8]; // x is share index

        // Evaluate polynomial at x for each byte of secret
        for &byte in secret {
            let mut y = byte as u32;
            let mut coeff = vec![byte as u32]; // constant term = secret byte
            coeff.resize(params.threshold, 0);

            // Random coefficients for degrees 1..t-1
            for i in 1..params.threshold {
                coeff[i] = rng.next_u32() % (FIELD_PRIME as u32);
            }

            // Evaluate poly(x) = c0 + c1*x + c2*x^2 + ... mod p
            let mut px = 1u32;
            for &c in &coeff {
                y = (y + c * px) % (FIELD_PRIME as u32);
                px = (px * (x as u32)) % (FIELD_PRIME as u32);
            }

            share.push(y as u8);
        }

        // Append hash for integrity
        let mut hasher = Keccak256::new();
        hasher.update(&share);
        let hash = hasher.finalize();
        share.extend_from_slice(&hash[..8]); // 8-byte checksum

        shares.push(share);
    }

    Ok(shares)
}

/// Reconstruct secret from at least threshold shares
pub fn reconstruct_secret(shares: &[Vec<u8>], threshold: usize) -> Result<Vec<u8>> {
    if shares.len() < threshold {
        return Err(ShamirError::NotEnoughShares {
            got: shares.len(),
            need: threshold,
        }.into());
    }

    // Assume all shares have same length
    let share_len = shares[0].len();
    if share_len < 9 { // x + at least 1 byte secret + 8 checksum
        return Err(anyhow!("Invalid share length"));
    }

    let mut secret = Vec::new();

    // For each byte position in secret
    for byte_idx in 1..(share_len - 8) {
        let mut points = Vec::new();

        for share in shares {
            let x = share[0] as i32;
            let y = share[byte_idx] as i32;
            points.push((x, y));
        }

        // Lagrange interpolation at x=0
        let mut result = 0i32;
        for (i, &(xi, yi)) in points.iter().enumerate() {
            let mut term = yi;
            for (j, &(xj, _)) in points.iter().enumerate() {
                if i != j {
                    let num = -xj;
                    let den = xi - xj;
                    term = (term * num * mod_inverse(den, FIELD_PRIME as i32)?) % (FIELD_PRIME as i32);
                }
            }
            result = (result + term) % (FIELD_PRIME as i32);
        }

        if result < 0 {
            result += FIELD_PRIME as i32;
        }
        secret.push(result as u8);
    }

    // Verify checksum on reconstructed secret
    let mut hasher = Keccak256::new();
    hasher.update(&secret);
    let computed_hash = hasher.finalize();
    let stored_hash = &shares[0][(share_len - 8)..];
    if &computed_hash[..8] != stored_hash {
        return Err(anyhow!("Checksum mismatch - corrupted shares"));
    }

    Ok(secret)
}

/// Modular inverse (extended Euclidean algorithm)
fn mod_inverse(a: i32, m: i32) -> Result<i32> {
    let (mut t, mut newt) = (0i32, 1i32);
    let (mut r, mut newr) = (m, a);

    while newr != 0 {
        let quotient = r / newr;
        (t, newt) = (newt, t - quotient * newt);
        (r, newr) = (newr, r - quotient * newr);
    }

    if r > 1 {
        return Err(ShamirError::PolyEvalError.into());
    }

    if t < 0 {
        t += m;
    }
    Ok(t)
              }
