// app/src/main/java/com/demoncore/soultracker/ui/DashboardViewModel.kt - Shared ViewModel for dashboard data
package com.demoncore.soultracker.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ViewModel() {

    private val _currentSoulTokenId = MutableStateFlow(BigInteger("1")) // Default demo
    val currentSoulTokenId = _currentSoulTokenId.asStateFlow()

    // Add more shared state: connected wallet, criticality, etc.
}
