# Demon Core 🔱 – Security & Threat Model (2026 edition)

## Critical assumptions

- The app is intended to run in high-adversary environments
- Users may be targeted by state-level actors, chain analysis firms, and occult-adjacent groups
- The criticality engine is deliberately non-deterministic and time-sensitive

## Attack surface summary

Category                  | Risk Level | Mitigation
-------------------------|------------|-----------------------------------------------
Private key / seed exposure | Critical   | Shamir secret sharing + hardware keystore fallback
Runtime key decryption     | High       | ChaCha20Poly1305 + Argon2id + runtime entropy
Anti-debug / anti-root     | High       | JNI anti-frida, ptrace checks, emulator detection
On-chain linkability       | Medium     | SIWE + unlinkable proofs planned (zk-SNARKs v0.2)
IPFS metadata leakage      | Medium     | Pinata JWT rotated every 72 h, metadata minimal
Criticality oracle abuse   | Very High  | Native Rust module + temporal bias + replay protection

## Hard rules (never violate)

1. Never store mnemonic/seed/private key in plaintext or simple SharedPreferences
2. Never log wallet addresses or token URIs in Logcat
3. Never allow minting without SIWE signature verification
4. Criticality > 0.96 → immediate local wipe (Room DB + EncryptedSharedPreferences)
5. All native crypto calls must be constant-time where possible

See SECURITY-AUDIT-PLAN.md for upcoming external review timeline.
