package com.demoncore.soultracker.core

class SoulScanner {
    fun scanSoul(): String {
        val resonanceLevel = (0..1000).random()
        return when {
            resonanceLevel > 900 -> "High resonance detected: Angelic alignment possible."
            resonanceLevel in 400..900 -> "Stable soul signature. No anomalies."
            else -> "Low resonance: Potential demonic interference. Criticality risk elevated."
        }
    }
}
