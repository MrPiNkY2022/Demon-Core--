use axum::{
    extract::{Json, State},
    http::StatusCode,
    response::IntoResponse,
    routing::post,
    Router,
};
use ethers::{
    prelude::*,
    providers::{Http, Middleware, Provider},
    signers::LocalWallet,
    types::TransactionRequest,
};
use serde::{Deserialize, Serialize};
use std::sync::Arc;
use tower_http::cors::CorsLayer;

#[derive(Clone)]
struct AppState {
    provider: Provider<Http>,
    wallet: LocalWallet,
    contract_addr: H160,
}

#[derive(Deserialize)]
struct MintRequest {
    to: String,
    uri: String,
    initial_karma: i64,
}

#[derive(Serialize)]
struct MintResponse {
    tx_hash: String,
}

async fn mint_soul(
    State(state): State<Arc<AppState>>,
    Json(req): Json<MintRequest>,
) -> impl IntoResponse {
    let to_addr = req.to.parse::<H160>().map_err(|e| (StatusCode::BAD_REQUEST, e.to_string()))?;

    // Call safeMint on SoulContract
    let calldata = // ABI encode safeMint(to, uri, initial_karma)
        // Use ethers-rs ABI encoder here
        vec![]; // Placeholder - use Contract ABI

    let tx = TransactionRequest::new()
        .to(state.contract_addr)
        .data(calldata)
        .from(state.wallet.address());

    let pending = state.provider.send_transaction(tx, None).await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))?;

    let receipt = pending.await
        .map_err(|e| (StatusCode::INTERNAL_SERVER_ERROR, e.to_string()))?;

    Json(MintResponse {
        tx_hash: format!("0x{}", hex::encode(receipt.transaction_hash.as_bytes())),
    })
}

#[tokio::main]
async fn main() -> Result<(), Box<dyn std::error::Error>> {
    let provider = Provider::<Http>::try_from("https://mainnet.infura.io/v3/YOUR_KEY")?;
    let wallet = "YOUR_PRIVATE_KEY".parse::<LocalWallet>()?; // Securely load from env/HSM

    let state = Arc::new(AppState {
        provider,
        wallet,
        contract_addr: "0xYourSoulContract".parse()?,
    });

    let app = Router::new()
        .route("/mint", post(mint_soul))
        .layer(CorsLayer::permissive())
        .with_state(state);

    let listener = tokio::net::TcpListener::bind("0.0.0.0:3000").await?;
    axum::serve(listener, app).await?;

    Ok(())
}
