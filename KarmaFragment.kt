lifecycleScope.launch {
    val karma = SoulBlockchainManager.getKarmaBalance(BigInteger("1")) // Token ID 1
    if (karma != null) {
        tvKarmaBalance.text = "On-chain Karma: $karma 🔱"
    } else {
        tvKarmaBalance.text = "Failed to fetch on-chain karma"
    }
}
