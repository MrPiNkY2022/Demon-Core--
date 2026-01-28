// app/src/main/java/com/demoncore/soultracker/ui/WalletSignStepFragment.kt
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.WalletConnectManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class WalletSignStepFragment : Fragment(), ValidatableStep {

    private lateinit var tvStatus: TextView
    private lateinit var btnConnect: Button
    private lateinit var btnSign: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_wallet_sign, container, false)

        tvStatus = view.findViewById(R.id.tv_wallet_status)
        btnConnect = view.findViewById(R.id.btn_connect_wallet)
        btnSign = view.findViewById(R.id.btn_sign_message)

        lifecycleScope.launch {
            WalletConnectManager.connectionStatus.collectLatest { status ->
                tvStatus.text = "Status: $status\nAddress: ${WalletConnectManager.walletAddress.value ?: "Not connected"}"
            }
        }

        btnConnect.setOnClickListener {
            WalletConnectManager.connect { uri ->
                tvStatus.text = "Scan this URI with your wallet:\n$uri"
                // In real app: show QR code here
            }
        }

        btnSign.setOnClickListener {
            lifecycleScope.launch {
                val message = "Demon Core Soul Mint - ${System.currentTimeMillis()}"
                val signature = WalletConnectManager.signMessage(message)
                if (signature != null) {
                    Toast.makeText(context, "Signed: ${signature.take(20)}...", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Sign failed or cancelled", Toast.LENGTH_LONG).show()
                }
            }
        }

        return view
    }

    override fun isValid(): Boolean {
        return !WalletConnectManager.walletAddress.value.isNullOrEmpty()
    }
}
