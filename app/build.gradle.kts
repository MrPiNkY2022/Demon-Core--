// Update to app/build.gradle.kts - Add blockchain integration dependencies
// Add these to the dependencies block (replace or append existing
// Add to app/build.gradle.kts dependencies block for new features

dependencies {
    // ... existing ...

    // WalletConnect v2 for Android (latest as of 2026)
    implementation("com.walletconnect:android-core:1.0.0-betaXX") // Check https://docs.walletconnect.com for exact version; use latest stable
    implementation("com.walletconnect:sign:1.0.0-betaXX") // Sign client for EIP-155
    implementation("com.walletconnect:auth:1.0.0-betaXX") // Optional for SIWE

    // CameraX
    implementation("androidx.camera:camera-core:1.3.4") // Latest stable
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // For IPFS metadata upload/fetch (optional for soul metadata)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
}

    // Web3j for Ethereum integration (Android-compatible version)
    implementation("org.web3j:core:4.12.0") // Use 4.12.0 or latest Android-friendly release

    // For HTTP requests (Infura/Alchemy RPC)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional for debugging
}
