// app/src/main/java/com/demoncore/soultracker/ui/MintSoulDialogFragment.kt
// New dialog: Guides user through minting a new soul token

package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.IPFSMetadataUploader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MintSoulDialogFragment : DialogFragment() {

    private lateinit var etSoulName: EditText
    private lateinit var etInitialKarma: EditText
    private lateinit var tvStatus: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_mint_soul, container, false)

        etSoulName = view.findViewById(R.id.et_soul_name)
        etInitialKarma = view.findViewById(R.id.et_initial_karma)
        tvStatus = view.findViewById(R.id.tv_mint_status)

        view.findViewById<Button>(R.id.btn_mint_soul).setOnClickListener {
            mintSoul()
        }

        return view
    }

    private fun mintSoul() {
        val name = etSoulName.text.toString().takeIf { it.isNotBlank() } ?: "Unnamed Soul"
        val initialKarma = etInitialKarma.text.toString().toIntOrNull() ?: 0

        tvStatus.text = "Generating metadata and minting... 🔱"

        CoroutineScope(Dispatchers.Main).launch {
            val metadata = mapOf(
                "name" to name,
                "description" to "A metaphysical soul tracked by Demon Core",
                "aura_level" to 50,
                "chakra_alignment" to 0.75,
                "reincarnation_count" to 3
            )

            val uri = withContext(Dispatchers.IO) { IPFSMetadataUploader.uploadMetadata(metadata) }
            if (uri != null) {
                // Call backend or direct mint (placeholder)
                tvStatus.text = "Soul minted! IPFS URI: $uri\nInitial Karma: $initialKarma"
            } else {
                tvStatus.text = "Failed to upload metadata to IPFS."
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
