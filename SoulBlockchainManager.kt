// app/src/main/java/com/demoncore/soultracker/blockchain/SoulBlockchainManager.kt
// Advanced blockchain manager for interacting with SoulContract (soulbound NFT for metaphysical souls)
// Uses Web3j for Ethereum RPC calls. Read-only functions are safe for Android client.
// Write operations (e.g., mint, updateKarma) should be handled server-side for security!

package com.demoncore.soultracker.blockchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.web3j.abi.FunctionEncoder
import org.web3j.abi.FunctionReturnDecoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Address
import org.web3j.abi.datatypes.Function
import org.web3j.abi.datatypes.Type
import org.web3j.abi.datatypes.generated.Int256
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthCall
import org.web3j.protocol.http.HttpService
import java.math.BigInteger

object SoulBlockchainManager {

    // Configuration - Update these with your deployed values!
    private const val INFURA_PROJECT_ID = "YOUR_INFURA_PROJECT_ID_HERE" // From https://infura.io
    private const val RPC_URL = "https://mainnet.infura.io/v3/$INFURA_PROJECT_ID" // Or sepolia for testnet
    private const val SOUL_CONTRACT_ADDRESS = "0xYourDeployedSoulContractAddressHere" // From deploy script

    // Partial ABI for key functions (expand as needed)
    private const val ABI_KARMA_BALANCE = """[{"constant":true,"inputs":[{"name":"tokenId","type":"uint256"}],"name":"karmaBalance","outputs":[{"name":"","type":"int256"}],"payable":false,"stateMutability":"view","type":"function"}]"""
    private const val ABI_TOKEN_URI = """[{"constant":true,"inputs":[{"name":"tokenId","type":"uint256"}],"name":"tokenURI","outputs":[{"name":"","type":"string"}],"payable":false,"stateMutability":"view","type":"function"}]"""
    private const val ABI_OWNER_OF = """[{"constant":true,"inputs":[{"name":"tokenId","type":"uint256"}],"name":"ownerOf","outputs":[{"name":"","type":"address"}],"payable":false,"stateMutability":"view","type":"function"}]"""

    private val web3j: Web3j = Web3j.build(HttpService(RPC_URL))

    /**
     * Get the karma balance for a soul token (on-chain immutable value).
     * @param tokenId The ID of the soul token (BigInteger)
     * @return The karma balance as BigInteger, or null on error
     */
    suspend fun getKarmaBalance(tokenId: BigInteger): BigInteger? = withContext(Dispatchers.IO) {
        try {
            val function = Function(
                "karmaBalance",
                listOf(Uint256(tokenId)),
                listOf<TypeReference<*>>(object : TypeReference<Int256>() {})
            )
            val encodedFunction = FunctionEncoder.encode(function)

            val ethCall = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                null, // from (not needed for view)
                SOUL_CONTRACT_ADDRESS,
                encodedFunction
            )

            val response: EthCall = web3j.ethCall(ethCall, DefaultBlockParameterName.LATEST).send()
            if (response.hasError()) {
                throw Exception("EthCall error: ${response.error.message}")
            }

            val result = FunctionReturnDecoder.decode(response.value, function.outputParameters)
            if (result.isNotEmpty() && result[0] is Int256) {
                (result[0] as Int256).value
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get the token URI (metadata) for a soul token.
     * Typically points to IPFS/JSON with aura, chakra, reincarnation data.
     */
    suspend fun getTokenURI(tokenId: BigInteger): String? = withContext(Dispatchers.IO) {
        try {
            val function = Function(
                "tokenURI",
                listOf(Uint256(tokenId)),
                listOf<TypeReference<*>>(object : TypeReference<Type<String>>() {})
            )
            val encodedFunction = FunctionEncoder.encode(function)

            val ethCall = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                null,
                SOUL_CONTRACT_ADDRESS,
                encodedFunction
            )

            val response: EthCall = web3j.ethCall(ethCall, DefaultBlockParameterName.LATEST).send()
            if (response.hasError()) return@withContext null

            val result = FunctionReturnDecoder.decode(response.value, function.outputParameters)
            if (result.isNotEmpty() && result[0] is String) {
                result[0] as String
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get the owner address of a soul token.
     */
    suspend fun getOwnerOf(tokenId: BigInteger): String? = withContext(Dispatchers.IO) {
        try {
            val function = Function(
                "ownerOf",
                listOf(Uint256(tokenId)),
                listOf<TypeReference<*>>(object : TypeReference<Address>() {})
            )
            val encodedFunction = FunctionEncoder.encode(function)

            val ethCall = org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
                null,
                SOUL_CONTRACT_ADDRESS,
                encodedFunction
            )

            val response: EthCall = web3j.ethCall(ethCall, DefaultBlockParameterName.LATEST).send()
            if (response.hasError()) return@withContext null

            val result = FunctionReturnDecoder.decode(response.value, function.outputParameters)
            if (result.isNotEmpty() && result[0] is Address) {
                (result[0] as Address).value
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get ETH balance of any address (useful for checking user's wallet before interactions).
     */
    suspend fun getEthBalance(address: String): BigInteger? = withContext(Dispatchers.IO) {
        try {
            val response = web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()
            if (response.hasError()) null else response.balance
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Placeholder for generating encoded data for a mint transaction.
     * This should be sent from backend (with private key) or via WalletConnect.
     * Returns the encoded calldata for safeMint(to, uri, initialKarma).
     */
    fun generateMintCalldata(
        toAddress: String,
        metadataUri: String,
        initialKarma: BigInteger
    ): String {
        val function = Function(
            "safeMint",
            listOf(
                Address(toAddress),
                org.web3j.abi.datatypes.Utf8String(metadataUri),
                Int256(initialKarma)
            ),
            emptyList()
        )
        return FunctionEncoder.encode(function)
    }

    // Future expansions:
    // - integrate WalletConnect for user signing
    // - add updateKarma calldata generator
    // - add event listening (karmaUpdated, soulMinted) via web3j subscription
    // - cache results with Room DB for offline support
}
