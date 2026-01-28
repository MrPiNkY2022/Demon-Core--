// app/src/main/java/com/demoncore/soultracker/ui/MintWizardFragment.kt
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.demoncore.soultracker.R
import com.demoncore.soultracker.blockchain.IPFSMetadataUploader
import com.demoncore.soultracker.blockchain.WalletConnectManager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigInteger

class MintWizardFragment : Fragment() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var btnPrevious: Button
    private lateinit var btnNext: Button

    private val viewModel: MintWizardViewModel by viewModels()

    private val steps = listOf(
        Step("Soul Basics", NameKarmaStepFragment::class.java),
        Step("Aura Scan", AuraScanStepFragment::class.java),
        Step("Wallet Connect", WalletSignStepFragment::class.java),
        Step("Confirm Mint", ConfirmMintStepFragment::class.java)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_mint_wizard, container, false)

        viewPager = view.findViewById(R.id.view_pager)
        tabLayout = view.findViewById(R.id.tab_layout)
        btnPrevious = view.findViewById(R.id.btn_previous)
        btnNext = view.findViewById(R.id.btn_next)

        setupViewPager()
        setupNavigationButtons()

        return view
    }

    private fun setupViewPager() {
        val adapter = WizardPagerAdapter(this, steps)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false  // Prevent swipe; use buttons only

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = steps[position].title
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtonStates(position)
            }
        })

        updateButtonStates(0)
    }

    private fun updateButtonStates(position: Int) {
        btnPrevious.isEnabled = position > 0
        btnNext.text = if (position == steps.size - 1) "Mint Soul" else "Next"
    }

    private fun setupNavigationButtons() {
        btnPrevious.setOnClickListener {
            val current = viewPager.currentItem
            if (current > 0) viewPager.currentItem = current - 1
        }

        btnNext.setOnClickListener {
            val current = viewPager.currentItem
            if (current < steps.size - 1) {
                // Validate current step before proceeding
                if ((viewPager.adapter as WizardPagerAdapter).validateStep(current)) {
                    viewPager.currentItem = current + 1
                } else {
                    Toast.makeText(context, "Please complete this step", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Final mint action
                performMint()
            }
        }
    }

    private fun performMint() {
        lifecycleScope.launch {
            val name = viewModel.soulName.value ?: "Unnamed Soul"
            val initialKarma = viewModel.initialKarma.value ?: 0
            val auraLevel = viewModel.auraLevel.value ?: 50
            val walletAddress = WalletConnectManager.walletAddress.value ?: ""

            if (walletAddress.isEmpty()) {
                Toast.makeText(context, "Please connect wallet first", Toast.LENGTH_LONG).show()
                return@launch
            }

            val metadata = mapOf(
                "name" to name,
                "description" to "Soul minted via Demon Core 🔱",
                "aura_level" to auraLevel,
                "karma" to initialKarma,
                "attributes" to listOf(
                    mapOf("trait_type" to "Wallet", "value" to walletAddress),
                    mapOf("trait_type" to "Mint Date", "value" to System.currentTimeMillis().toString())
                )
            )

            val uri = withContext(Dispatchers.IO) {
                IPFSMetadataUploader.uploadMetadata(metadata)
            }

            if (uri == null) {
                Toast.makeText(context, "Failed to upload metadata to IPFS", Toast.LENGTH_LONG).show()
                return@launch
            }

            // TODO: Call backend mint endpoint or use WalletConnect to sign/send tx
            // For now: simulate success
            Toast.makeText(
                context,
                "Soul minted successfully!\nURI: $uri\nKarma: $initialKarma",
                Toast.LENGTH_LONG
            ).show()

            // Optional: Navigate back or to dashboard
            parentFragmentManager.popBackStack()
        }
    }
}

// Data class for wizard steps
data class Step(val title: String, val fragmentClass: Class<out Fragment>)

// Simple pager adapter
class WizardPagerAdapter(
    fragment: Fragment,
    private val steps: List<Step>
) : androidx.fragment.app.FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = steps.size

    override fun createFragment(position: Int): Fragment {
        return steps[position].fragmentClass.getDeclaredConstructor().newInstance()
    }

    fun validateStep(position: Int): Boolean {
        // Call validation on the current fragment if it implements Validatable
        val fragment = (fragment as? MintWizardFragment)
            ?.childFragmentManager
            ?.fragments
            ?.getOrNull(position) as? ValidatableStep
        return fragment?.isValid() ?: true
    }
}

// Interface for steps that need validation
interface ValidatableStep {
    fun isValid(): Boolean
}
