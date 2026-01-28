// app/src/main/java/com/demoncore/soultracker/ui/ConfirmMintStepFragment.kt
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.demoncore.soultracker.R

class ConfirmMintStepFragment : Fragment(), ValidatableStep {

    private val viewModel: MintWizardViewModel by activityViewModels()

    private lateinit var tvSummary: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_confirm_mint, container, false)

        tvSummary = view.findViewById(R.id.tv_mint_summary)

        updateSummary()

        return view
    }

    private fun updateSummary() {
        val name = viewModel.soulName.value ?: "—"
        val karma = viewModel.initialKarma.value ?: 0
        val aura = viewModel.auraLevel.value ?: "—"

        tvSummary.text = buildString {
            append("Soul Mint Summary\n\n")
            append("Name: $name\n")
            append("Initial Karma: $karma\n")
            append("Detected Aura Level: $aura%\n\n")
            append("Wallet: ${WalletConnectManager.walletAddress.value ?: "Not connected"}\n")
            append("\nReady to mint this soul on-chain?")
        }
    }

    override fun isValid(): Boolean {
        // All previous steps should already be validated
        return true
    }
}
