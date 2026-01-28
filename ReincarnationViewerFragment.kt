// app/src/main/java/com/demoncore/soultracker/ui/ReincarnationViewerFragment.kt
// New fragment: Displays a timeline of past and future reincarnations from on-chain registry

package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.SoulBlockchainManager
import kotlinx.coroutines.launch
import java.math.BigInteger

class ReincarnationViewerFragment : Fragment() {

    private lateinit var tvPastLives: TextView
    private lateinit var tvFutureLives: TextView
    private lateinit var tvTimelineStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reincarnation_viewer, container, false)

        tvPastLives = view.findViewById(R.id.tv_past_lives)
        tvFutureLives = view.findViewById(R.id.tv_future_lives)
        tvTimelineStatus = view.findViewById(R.id.tv_timeline_status)

        loadReincarnationData()

        view.findViewById<TextView>(R.id.btn_refresh_reincarnation).setOnClickListener {
            loadReincarnationData()
        }

        return view
    }

    private fun loadReincarnationData() {
        tvTimelineStatus.text = "Querying multiverse timeline... 🔱"

        lifecycleScope.launch {
            val tokenId = BigInteger("1") // Demo: use connected wallet's soul token ID later
            val pastLives = SoulBlockchainManager.getPastLives(tokenId) // Assume added method to manager
            val futureLives = SoulBlockchainManager.getFutureLives(tokenId)

            tvPastLives.text = if (pastLives.isNotEmpty()) {
                "Past Lives:\n" + pastLives.joinToString("\n") { "Token #$it - Karmic link" }
            } else "No past lives detected."

            tvFutureLives.text = if (futureLives.isNotEmpty()) {
                "Future Lives:\n" + futureLives.joinToString("\n") { "Predicted Token #$it - Ascension path" }
            } else "No future branches predicted yet."

            tvTimelineStatus.text = "Timeline loaded from on-chain registry."
        }
    }
}
