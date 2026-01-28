// Update to app/build.gradle.kts - Add blockchain integration dependencies
// Add these to the dependencies block (replace or append existing)

dependencies {
    // ... existing dependencies ...

    // Web3j for Ethereum integration (Android-compatible version)
    implementation("org.web3j:core:4.12.0") // Use 4.12.0 or latest Android-friendly release

    // For HTTP requests (Infura/Alchemy RPC)
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // Optional for debugging
}
