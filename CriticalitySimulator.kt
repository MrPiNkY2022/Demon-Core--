// app/src/main/java/com/demoncore/soultracker/core/CriticalitySimulator.kt
// New core simulator: Predicts demonic criticality based on combined metrics

package com.demoncore.soultracker.core

import kotlinx.coroutines.delay
import kotlin.random.Random

object CriticalitySimulator {

    data class CriticalityEvent(val level: Int, val message: String, val isCritical: Boolean)

    suspend fun simulateCriticality(karma: Int, auraLevel: Int, chakraAvg: Double): CriticalityEvent {
        delay(2000) // Simulate computation time

        val score = (karma * 0.4) + (auraLevel * 0.3) + (chakraAvg * 20) // Weighted
        val randomFactor = Random.nextInt(-20, 21)

        val finalScore = (score + randomFactor).toInt().coerceIn(-100, 100)

        return when {
            finalScore > 70 -> CriticalityEvent(0, "Stable: No criticality. Soul in harmony.", false)
            finalScore in 30..70 -> CriticalityEvent(1, "Low risk: Minor fluctuations detected.", false)
            finalScore in -30..30 -> CriticalityEvent(2, "Medium: Entanglement instability. Monitor closely.", false)
            finalScore in -70..-30 -> CriticalityEvent(3, "High: Infernal resonance rising! Criticality imminent.", true)
            else -> CriticalityEvent(4, "SUPERNOVA: Demon Core supercritical! Evacuate plane! 🔱", true)
        }
    }
}
