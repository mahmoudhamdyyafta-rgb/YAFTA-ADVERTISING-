package com.aistudio.yafta.branding.data

import com.aistudio.yafta.branding.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@Serializable
data class Content(
    val parts: List<Part>
)

@Serializable
data class Part(
    val text: String? = null
)

@Serializable
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val topK: Int? = null
)

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>? = null
)

@Serializable
data class Candidate(
    val content: Content
)

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val json = Json { ignoreUnknownKeys = true }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateSlogansAndDesigns(businessType: String, description: String, textPrompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "YOUR_GEMINI_API_KEY_HERE" || apiKey == "GEMINI_API_KEY") {
            return@withContext "API Key is missing or not configured. Please add your GEMINI_API_KEY in the Secrets panel in AI Studio settings to enable Gemini features."
        }

        val finalPrompt = """
            You are an expert signage and advertising brand strategist.
            The user runs a signage and advertising manufacturing company 'Yafta for Advertising'.
            We want design proposals and creative slogans for a client with:
            - Business Type: $businessType
            - Business Description: $description
            - Additional client requirements: $textPrompt

            Please provide:
            1. Slogan Ideas: Propose 3 catchy, high-impact marketing slogans suitable for display on signs and lightboxes.
            2. Signage Design Concept: Propose a design layout (choosing between 3D acrylic glowing letters, neon flex, flex billboard, or backlit light box), suggesting recommended colors (neon/high contrast matching modern trends), dimensions, and material list.
            3. Installation Tip: Propose where and how to safely install this sign for maximum visual exposure.

            Be highly professional, clear, and structured. Use bullet points. Keep it practical and inspiring!
        """.trimIndent()

        val requestBodyData = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = finalPrompt))))
        )

        try {
            val requestBodyString = json.encodeToString(requestBodyData)
            val mediaType = "application/json".toMediaType()
            val requestBody = requestBodyString.toRequestBody(mediaType)

            val request = Request.Builder()
                .url("${BASE_URL}v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey")
                .post(requestBody)
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext "Error calling Gemini API: HTTP ${response.code} ${response.message}."
                }
                val bodyString = response.body?.string()
                if (bodyString.isNullOrEmpty()) {
                    return@withContext "Error calling Gemini API: Empty response body."
                }
                val responseObject = json.decodeFromString<GenerateContentResponse>(bodyString)
                responseObject.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "No suggestions could be generated. Please try again."
            }
        } catch (e: Exception) {
            "Error calling Gemini API: ${e.localizedMessage}. Please ensure your API Key is valid and internet access is active."
        }
    }
}
