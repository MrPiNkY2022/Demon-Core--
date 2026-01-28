// WalletConnect v2 Manager for user wallet connection (sign transactions, view accounts)
package com.demoncore.soultracker.blockchain

import android.content.Context
import com.walletconnect.android.Core
import com.walletconnect.android.CoreClient
import com.walletconnect.android.relay.RelayClient
import com.walletconnect.sign.client.Sign
import com.walletconnect.sign.client.SignClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object WalletConnectManager {

    private var core: CoreClient? = null
    private var signClient: SignClient? = null

    private val _connectionStatus = MutableStateFlow("Disconnected")
    val connectionStatus = _connectionStatus.asStateFlow()

    private val _walletAddress = MutableStateFlow<String?>(null)
    val walletAddress = _walletAddress.asStateFlow()

    fun init(context: Context, projectId: String) { // Get projectId from https://cloud.walletconnect.com
        CoreClient.initialize(
            Core.Params.Init(
                application = context.applicationContext,
                relayServerUrl = RelayClient.relayUrl,
                connectionType = Core.Params.ConnectionType.MANUAL,
                metaData = Core.Model.AppMetaData(
                    name = "Demon Core Soul Tracker",
                    description = "Metaphysical soul tracking DApp",
                    url = "https://github.com/yourusername/Demon-Core-🔱",
                    icons = listOf("https://your-icon-url.com/icon.png")
                )
            ),
            onSuccess = { /* Initialized */ },
            onError = { /* Handle error */ }
        )

        SignClient.initialize(
            Sign.Params.Init(core = CoreClient),
            onSuccess = {
                signClient = it
                setupListeners()
            },
            onError = { /* Handle */ }
        )
    }

    private fun setupListeners() {
        signClient?.let { client ->
            client.sessionState.observe { session ->
                if (session != null) {
                    _connectionStatus.value = "Connected"
                    _walletAddress.value = session.namespaces["eip155"]?.accounts?.firstOrNull()?.address
                } else {
                    _connectionStatus.value = "Disconnected"
                    _walletAddress.value = null
                }
            }
        }
    }

    // Connect: Launch QR or deep link to wallet
    fun connect(onUri: (String) -> Unit) {
        signClient?.pair(
            Sign.Params.Pair(uri = "wc:...", /* Generate pairing URI via client.pair() or from QR */),
            onSuccess = { /* Paired */ },
            onError = { /* */ }
        )
        // Typically, display QR code with pairing URI
        // Use a library like ZXing to generate QR from uri
    }

    // Example: Request personal_sign for authentication
    suspend fun signMessage(message: String): String? {
        // Use signClient.request to propose sign
        // This is async and requires user approval in wallet
        return null // Placeholder; implement full flow
    }

    // Disconnect
    fun disconnect() {
        signClient?.disconnect(Sign.Params.Disconnect(sessionTopic = "topic_here"), onSuccess = {}, onError = {})
    }
}
