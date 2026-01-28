// app/src/main/java/com/demoncore/soultracker/ui/AuraScanStepFragment.kt
package com.demoncore.soultracker.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.demoncore.soultracker.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AuraScanStepFragment : Fragment(), ValidatableStep {

    private val viewModel: MintWizardViewModel by activityViewModels()

    private lateinit var previewView: PreviewView
    private lateinit var tvResult: TextView
    private lateinit var btnStartScan: Button

    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_step_aura_scan, container, false)

        previewView = view.findViewById(R.id.preview_view)
        tvResult = view.findViewById(R.id.tv_aura_result)
        btnStartScan = view.findViewById(R.id.btn_start_aura_scan)

        btnStartScan.setOnClickListener {
            if (allPermissionsGranted()) {
                startCameraAnalysis()
            } else {
                requestPermissions()
            }
        }

        // Pre-fill if already scanned
        viewModel.auraLevel.value?.let {
            tvResult.text = "Aura Level captured: $it%"
        }

        return view
    }

    private fun startCameraAnalysis() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder().build().also {
                it.setAnalyzer(cameraExecutor) { imageProxy ->
                    // Very simple simulation – in real app use ML model or color analysis
                    val simulatedLevel = (30..95).random()
                    activity?.runOnUiThread {
                        tvResult.text = "Aura Level detected: $simulatedLevel%"
                        viewModel.setAuraLevel(simulatedLevel)
                    }
                    imageProxy.close()
                }
            }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer)
            } catch (exc: Exception) {
                tvResult.text = "Camera error: ${exc.message}"
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            REQUIRED_PERMISSIONS,
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCameraAnalysis()
            } else {
                Toast.makeText(context, "Camera permission is required for aura scan", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun isValid(): Boolean {
        return viewModel.auraLevel.value != null
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 101
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}
