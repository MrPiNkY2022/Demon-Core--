// app/src/main/java/com/demoncore/soultracker/ui/WalletConnectFragment.kt
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.WalletConnectManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WalletConnectFragment : Fragment() {

    private lateinit var tvStatus: TextView
    private lateinit var btnConnect: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_wallet_connect, container, false)

        tvStatus = view.findViewById(R.id.tv_wallet_status)
        btnConnect = view.findViewById(R.id.btn_connect_wallet)

        lifecycleScope.launch {
            WalletConnectManager.connectionStatus.collectLatest { status ->
                tvStatus.text = "Wallet: $status\nAddress: ${WalletConnectManager.walletAddress.value ?: "N/A"}"
            }
        }

        btnConnect.setOnClickListener {
            WalletConnectManager.connect { uri ->
                // Display QR code from uri (use ZXing or library)
                tvStatus.text = "Scan QR with wallet: $uri"
            }
        }

        return view
    }
}
