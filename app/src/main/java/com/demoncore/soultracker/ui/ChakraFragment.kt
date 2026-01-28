// app/src/main/java/com/demoncore/soultracker/ui/ChakraFragment.kt
// New Chakra Fragment: Visualizes 7 chakras with alignment scores and on-chain soul data integration

package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.SoulBlockchainManager
import kotlinx.coroutines.launch
import java.math.BigInteger

class ChakraFragment : Fragment() {

    private lateinit var tvChakraStatus: TextView
    private lateinit var pbRoot: ProgressBar
    private lateinit var pbSacral: ProgressBar
    private lateinit var pbSolar: ProgressBar
    private lateinit var pbHeart: ProgressBar
    private lateinit var pbThroat: ProgressBar
    private lateinit var pbThirdEye: ProgressBar
    private lateinit var pbCrown: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chakra, container, false)

        tvChakraStatus = view.findViewById(R.id.tv_chakra_status)
        pbRoot = view.findViewById(R.id.pb_root_chakra)
        pbSacral = view.findViewById(R.id.pb_sacral_chakra)
        pbSolar = view.findViewById(R.id.pb_solar_chakra)
        pbHeart = view.findViewById(R.id.pb_heart_chakra)
        pbThroat = view.findViewById(R.id.pb_throat_chakra)
        pbThirdEye = view.findViewById(R.id.pb_third_eye_chakra)
        pbCrown = view.findViewById(R.id.pb_crown_chakra)

        // Initial load
        loadChakraAlignment()

        // Button to refresh from on-chain (assuming token ID 1 for demo)
        view.findViewById<TextView>(R.id.btn_refresh_chakra).setOnClickListener {
            loadChakraAlignment()
        }

        return view
    }

    private fun loadChakraAlignment() {
        tvChakraStatus.text = "Aligning chakras with astral plane... 🔮"

        lifecycleScope.launch {
            val karma = SoulBlockchainManager.getKarmaBalance(BigInteger("1")) // Example token ID
            if (karma != null) {
                val alignmentScore = (karma.toInt() + 100) / 2 // Normalize to 0-100
                updateChakraProgress(alignmentScore)
                tvChakraStatus.text = "On-chain Karma: $karma\nOverall Alignment: $alignmentScore%"
            } else {
                // Fallback to simulated data
                updateChakraProgress((0..100).random())
                tvChakraStatus.text = "Failed to fetch on-chain data. Using simulated alignment."
            }
        }
    }

    private fun updateChakraProgress(overall: Int) {
        // Simulate per-chakra scores based on overall
        pbRoot.progress = (overall * 0.8 + (0..20).random()).toInt().coerceIn(0, 100)
        pbSacral.progress = (overall * 0.9 + (0..15).random()).toInt().coerceIn(0, 100)
        pbSolar.progress = (overall + (0..30).random()).toInt().coerceIn(0, 100)
        pbHeart.progress = (overall * 1.1 + (0..20).random()).toInt().coerceIn(0, 100)
        pbThroat.progress = (overall + (0..25).random()).toInt().coerceIn(0, 100)
        pbThirdEye.progress = (overall * 1.2 + (0..15).random()).toInt().coerceIn(0, 100)
        pbCrown.progress = (overall * 1.3 + (0..10).random()).toInt().coerceIn(0, 100)
    }
}
