// app/src/main/java/com/demoncore/soultracker/ui/NameKarmaStepFragment.kt
package com.demoncore.soultracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.demoncore.soultracker.R

class NameKarmaStepFragment : Fragment(), ValidatableStep {

    private val viewModel: MintWizardViewModel by activityViewModels()

    private lateinit var etName: EditText
    private lateinit var etKarma: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_name_karma, container, false)

        etName = view.findViewById(R.id.et_soul_name)
        etKarma = view.findViewById(R.id.et_initial_karma)

        // Pre-fill if already set
        viewModel.soulName.value?.let { etName.setText(it) }
        viewModel.initialKarma.value?.let { etKarma.setText(it.toString()) }

        return view
    }

    override fun onPause() {
        super.onPause()
        // Save values when leaving the step
        viewModel.setSoulName(etName.text.toString().trim())
        etKarma.text.toString().toIntOrNull()?.let { viewModel.setInitialKarma(it) }
    }

    override fun isValid(): Boolean {
        val name = etName.text.toString().trim()
        val karmaStr = etKarma.text.toString().trim()

        return when {
            name.isEmpty() -> {
                etName.error = "Soul name is required"
                false
            }
            karmaStr.isEmpty() || karmaStr.toIntOrNull() == null -> {
                etKarma.error = "Enter a valid number for initial karma"
                false
            }
            else -> true
        }
    }
}
