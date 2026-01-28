// app/src/main/java/com/demoncore/soultracker/blockchain/SoulMetadataFetcher.kt
package com.demoncore.soultracker.blockchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

object SoulMetadataFetcher {

    private val client = OkHttpClient()

    /**
     * Fetch metadata JSON from tokenURI (e.g., IPFS or HTTP).
     * Returns parsed map with soul data like aura_level, reincarnation_count, etc.
     */
    suspend fun fetchMetadata(uri: String): Map<String, Any>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(uri).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val jsonString = response.body?.string() ?: return@withContext null
            val json = JSONObject(jsonString)

            mapOf(
                "name" to (json.optString("name", "Unknown Soul")),
                "description" to (json.optString("description", "")),
                "aura_level" to (json.optInt("aura_level", 0)),
                "chakra_alignment" to (json.optDouble("chakra_alignment", 0.0)),
                "reincarnation_count" to (json.optInt("reincarnation_count", 0)),
                "image" to (json.optString("image", ""))
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

