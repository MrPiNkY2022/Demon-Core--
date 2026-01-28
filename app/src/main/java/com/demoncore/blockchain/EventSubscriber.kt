// app/src/main/java/com/demoncore/soultracker/blockchain/EventSubscriber.kt
// New file: Subscribes to contract events using Web3j (e.g., KarmaUpdated, SoulMinted)

package com.demoncore.soultracker.blockchain

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.web3j.abi.EventEncoder
import org.web3j.abi.TypeReference
import org.web3j.abi.datatypes.Event
import org.web3j.abi.datatypes.generated.Int256
import org.web3j.abi.datatypes.generated.Uint256
import org.web3j.protocol.core.methods.request.EthFilter
import org.web3j.protocol.core.methods.response.Log
import java.math.BigInteger

object EventSubscriber {

    private val _karmaUpdateFlow = MutableSharedFlow<Pair<BigInteger, BigInteger>>()
    val karmaUpdateFlow = _karmaUpdateFlow.asSharedFlow()

    private val _soulMintedFlow = MutableSharedFlow<Pair<BigInteger, String>>()
    val soulMintedFlow = _soulMintedFlow.asSharedFlow()

    suspend fun subscribeToEvents() {
        val web3j = SoulBlockchainManager.web3j // Reuse existing instance

        // KarmaUpdated event: tokenId (uint256), oldKarma (int256), newKarma (int256)
        val karmaEvent = Event(
            "KarmaUpdated",
            listOf(
                Uint256::class.java,
                Int256::class.java,
                Int256::class.java
            )
        )
        val karmaFilter = EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, SOUL_CONTRACT_ADDRESS)
        karmaFilter.addSingleTopic(EventEncoder.encode(karmaEvent))

        web3j.ethLogFlowable(karmaFilter).subscribe { log: Log ->
            val decoded = org.web3j.abi.FunctionReturnDecoder.decodeIndexedValue(log.topics[1], Uint256::class.java) as Uint256
            val oldKarma = org.web3j.abi.FunctionReturnDecoder.decodeIndexedValue(log.data.substring(0, 66), Int256::class.java) as Int256
            val newKarma = org.web3j.abi.FunctionReturnDecoder.decodeIndexedValue(log.data.substring(66), Int256::class.java) as Int256
            _karmaUpdateFlow.tryEmit(decoded.value to newKarma.value)
        }

        // SoulMinted event: tokenId, owner
        val mintedEvent = Event(
            "SoulMinted",
            listOf(
                Uint256::class.java,
                org.web3j.abi.datatypes.Address::class.java
            )
        )
        val mintedFilter = EthFilter(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST, SOUL_CONTRACT_ADDRESS)
        mintedFilter.addSingleTopic(EventEncoder.encode(mintedEvent))

        web3j.ethLogFlowable(mintedFilter).subscribe { log: Log ->
            val tokenId = org.web3j.abi.FunctionReturnDecoder.decodeIndexedValue(log.topics[1], Uint256::class.java) as Uint256
            val owner = org.web3j.abi.FunctionReturnDecoder.decodeIndexedValue(log.topics[2], org.web3j.abi.datatypes.Address::class.java) as org.web3j.abi.datatypes.Address
            _soulMintedFlow.tryEmit(tokenId.value to owner.value)
        }
    }
}
