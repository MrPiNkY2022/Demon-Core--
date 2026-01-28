// app/src/main/java/com/demoncore/soultracker/ui/DashboardFragment.kt - New central dashboard fragment
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.demoncore.soultracker.R
import com.demoncore.soultracker.core.CriticalitySimulator
import com.demoncore.soultracker.blockchain.SoulBlockchainManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import kotlinx.coroutines.launch
import java.math.BigInteger
import com.airbnb.lottie.LottieAnimationView
import android.os.Vibrator
import android.os.VibrationEffect
import android.content.Context.VIBRATOR_SERVICE

class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by activityViewModels() // Shared VM

    private lateinit var tvKarma: TextView
    private lateinit var tvAura: TextView
    private lateinit var tvCriticality: TextView
    private lateinit var progressCriticality: CircularProgressIndicator
    private lateinit var cardWallet: MaterialCardView
    private lateinit var tvWalletAddress: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        tvKarma = view.findViewById(R.id.tv_karma_value)
        tvAura = view.findViewById(R.id.tv_aura_value)
        tvCriticality = view.findViewById(R.id.tv_criticality_level)
        progressCriticality = view.findViewById(R.id.progress_criticality)
        cardWallet = view.findViewById(R.id.card_wallet)
        tvWalletAddress = view.findViewById(R.id.tv_wallet_address)

        loadDashboardData()

        cardWallet.setOnClickListener {
            // Navigate to wallet fragment
            findNavController().navigate(R.id.walletFragment)
        }

        return view
    }

    private fun loadDashboardData() {
        lifecycleScope.launch {
            // Fetch on-chain data
            val karma = SoulBlockchainManager.getKarmaBalance(BigInteger("1"))?.toInt() ?: 0
            val auraLevel = 65 // Placeholder; fetch from AuraContract or metadata
            val chakraAvg = 0.78 // From ChakraFragment logic

            tvKarma.text = "$karma"
            tvAura.text = "$auraLevel%"

            val criticality = CriticalitySimulator.simulateCriticality(karma, auraLevel, chakraAvg)
            tvCriticality.text = criticality.message
            progressCriticality.progress = criticality.level * 25 // 0-100 scale
            progressCriticality.setIndicatorColor(
                when (criticality.level) {
                    0 -> 0xFF00FF00.toInt() // Green
                    1 -> 0xFFFFFF00.toInt() // Yellow
                    2 -> 0xFFFFA500.toInt() // Orange
                    3 -> 0xFFFF0000.toInt() // Red
                    else -> 0xFF8B0000.toInt() // Dark red
                }
            )
        }

   private fun updateCriticality(criticality: CriticalitySimulator.CriticalityEvent) {
               val lottie = view?.findViewById<LottieAnimationView>(R.id.lottie_criticality)
    lottie?.apply {
        when (criticality.level) {
            in 3..4 -> {
                setAnimation(R.raw.criticality_high)  // red pulsing ring
                playAnimation()
                // Vibrate
                val vibrator = requireContext().getSystemService(VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 100, 300), -1))
                }
            }
            else -> {
                setAnimation(R.raw.criticality_low)   // calm blue
                playAnimation()
            }
        }
    }
    tvCriticality.text = criticality.message
}
