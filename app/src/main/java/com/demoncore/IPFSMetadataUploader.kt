// app/src/main/java/com/demoncore/soultracker/blockchain/IPFSMetadataUploader.kt
// New file: Utility to upload soul metadata JSON to IPFS (via Pinata or similar gateway)
// Requires Retrofit for HTTP POST to IPFS pinning service

package com.demoncore.soultracker.blockchain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

object IPFSMetadataUploader {

    private const val PINATA_API_URL = "https://api.pinata.cloud/"
    private const val PINATA_JWT = "YOUR_PINATA_JWT_HERE" // Get from pinata.cloud (never hardcode in prod!)

    private val retrofit = Retrofit.Builder()
        .baseUrl(PINATA_API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val pinataService = retrofit.create(PinataService::class.java)

    suspend fun uploadMetadata(metadata: Map<String, Any>): String? = withContext(Dispatchers.IO) {
        try {
            val json = JSONObject(metadata).toString()
            val jsonBody = json.toRequestBody("application/json".toMediaType())

            val file = File.createTempFile("soul_metadata", ".json")
            file.writeText(json)

            val requestFile = file.asRequestBody("application/json".toMediaType())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            val response = pinataService.pinFileToIPFS(
                authorization = "Bearer $PINATA_JWT",
                file = body
            )

            file.delete() // Cleanup

            if (response.isSuccessful) {
                val ipfsHash = response.body()?.IpfsHash ?: return@withContext null
                "ipfs://$ipfsHash"
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    interface PinataService {
        @Multipart
        @POST("pinning/pinFileToIPFS")
        suspend fun pinFileToIPFS(
            @retrofit2.http.Header("Authorization") authorization: String,
            @Part file: MultipartBody.Part
        ): retrofit2.Response<PinataResponse>
    }

    data class PinataResponse(val IpfsHash: String)
}
