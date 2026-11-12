package com.example.audio

import android.content.Context
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.TimeUnit

data class GeminiVoice(
    val name: String,
    val label: String,
    val description: String
)

class GeminiTtsService(private val context: Context) {

    companion object {
        val PREBUILT_VOICES = listOf(
            GeminiVoice("Charon", "Charon (Informative)", "Deep, calm, measured storyteller"),
            GeminiVoice("Gacrux", "Gacrux (Mature)", "Deep, seasoned, authoritative tone"),
            GeminiVoice("Sadaltager", "Sadaltager (Knowledgeable)", "Wise, experienced, intellectual"),
            GeminiVoice("Algenib", "Algenib (Gravelly)", "Resonant, textured, grounded presence"),
            GeminiVoice("Orus", "Orus (Firm)", "Decisive, disciplined, steady mentor"),
            GeminiVoice("Iapetus", "Iapetus (Clear)", "Crisp, articulated, focused delivery"),
            GeminiVoice("Rasalgethi", "Rasalgethi (Informative)", "Measured, thoughtful, analytical"),
            GeminiVoice("Sulafat", "Sulafat (Warm)", "Supportive, inviting, compassionate"),
            GeminiVoice("Achird", "Achird (Friendly)", "Approachable, conversational, upbeat"),
            GeminiVoice("Fenrir", "Fenrir (Leader)", "Deep, resonant, commanding power"),
            GeminiVoice("Puck", "Puck (Energetic)", "Expressive, engaging, vibrant"),
            GeminiVoice("Kore", "Kore (Gentle)", "Warm, soothing, encouraging"),
            GeminiVoice("Aoede", "Aoede (Poised)", "Melodic, inspiring, articulate")
        )

        const val QUOTA_LIMIT_MESSAGE = "Daily free voice limit reached. Resets around 9am. Using phone voice for now."

        private const val PRIMARY_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash-tts:generateContent"
        private const val FALLBACK_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-flash-lite-tts:generateContent"
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    fun getMorningAudioFile(): File {
        return File(context.filesDir, "rise_morning_briefing.wav")
    }

    fun getEveningAudioFile(): File {
        return File(context.filesDir, "rise_evening_reflection.wav")
    }

    fun getPreviewAudioFile(): File {
        return File(context.filesDir, "rise_voice_preview.wav")
    }

    fun getVoicePreviewAudioFile(voiceName: String): File {
        val safeName = voiceName.trim().lowercase().replace("[^a-z0-9]".toRegex(), "_")
        return File(context.filesDir, "rise_preview_$safeName.wav")
    }

    private fun buildJsonBody(text: String, voiceName: String, voiceStyle: String): String {
        val fullPrompt = "Read the following message aloud. Style instructions: $voiceStyle\n\nMessage to read:\n$text"
        val jsonBody = JSONObject().apply {
            val contentsArray = JSONArray().apply {
                put(JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().put("text", fullPrompt))
                    }
                    put("parts", partsArray)
                })
            }
            put("contents", contentsArray)

            val genConfig = JSONObject().apply {
                val modalities = JSONArray().apply { put("AUDIO") }
                put("responseModalities", modalities)

                val speechConfig = JSONObject().apply {
                    val voiceConfig = JSONObject().apply {
                        val prebuiltVoiceConfig = JSONObject().apply {
                            put("voiceName", voiceName)
                        }
                        put("prebuiltVoiceConfig", prebuiltVoiceConfig)
                    }
                    put("voiceConfig", voiceConfig)
                }
                put("speechConfig", speechConfig)
            }
            put("generationConfig", genConfig)
        }
        return jsonBody.toString()
    }

    private fun executeCall(url: String, apiKey: String, jsonString: String): Pair<Int, String> {
        val requestBody = jsonString.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url("$url?key=$apiKey")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            val code = response.code
            val body = response.body?.string() ?: ""
            return Pair(code, body)
        }
    }

    private fun isQuotaError(code: Int, body: String): Boolean {
        if (code == 429) return true
        if (body.contains("RESOURCE_EXHAUSTED", ignoreCase = true)) return true
        if (body.contains("quota", ignoreCase = true) && body.contains("exceeded", ignoreCase = true)) return true
        return false
    }

    suspend fun generateSpeech(
        apiKey: String,
        text: String,
        voiceName: String,
        voiceStyle: String,
        outputFile: File
    ): Result<File> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("Gemini API key is missing."))
        }

        try {
            val jsonString = buildJsonBody(text, voiceName, voiceStyle)

            // Step 1: Call primary speech model
            var (code, responseBody) = executeCall(PRIMARY_URL, apiKey, jsonString)

            // Step 2: If primary model hits quota (429), try fallback model "gemini-3.8-flash-lite-tts" once
            if (isQuotaError(code, responseBody)) {
                val fallback = executeCall(FALLBACK_URL, apiKey, jsonString)
                code = fallback.first
                responseBody = fallback.second
            }

            // Step 3: If still quota exceeded, return friendly limit message without retrying
            if (isQuotaError(code, responseBody)) {
                return@withContext Result.failure(Exception(QUOTA_LIMIT_MESSAGE))
            }

            if (code !in 200..299) {
                val errorDetail = try {
                    val json = JSONObject(responseBody)
                    val errorObj = json.optJSONObject("error")
                    errorObj?.optString("message") ?: responseBody
                } catch (e: Exception) {
                    responseBody
                }
                return@withContext Result.failure(Exception("Gemini API error ($code): $errorDetail"))
            }

            val responseJson = JSONObject(responseBody)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No candidates returned from Gemini API."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            if (parts == null || parts.length() == 0) {
                return@withContext Result.failure(Exception("No content parts in Gemini response."))
            }

            var audioBase64: String? = null
            var mimeType: String? = null

            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                val inlineData = part.optJSONObject("inlineData")
                if (inlineData != null) {
                    audioBase64 = inlineData.optString("data")
                    mimeType = inlineData.optString("mimeType", null)
                    break
                }
            }

            if (audioBase64.isNullOrBlank()) {
                return@withContext Result.failure(Exception("No audio data found in Gemini response."))
            }

            val rawBytes = Base64.decode(audioBase64, Base64.DEFAULT)

            // Extract sample rate from mimeType if provided (e.g. "audio/pcm;rate=24000"), defaulting to 24000
            val sampleRate = parseSampleRate(mimeType, 24000)

            // Ensure raw 16-bit mono PCM is properly wrapped with a standard 44-byte WAV header
            val finalBytes = if (isWavOrMp3Header(rawBytes)) {
                rawBytes
            } else {
                wrapPcmInWav(rawBytes, sampleRate = sampleRate, channels = 1, bitsPerSample = 16)
            }

            if (outputFile.exists()) {
                outputFile.delete()
            }
            FileOutputStream(outputFile).use { it.write(finalBytes) }

            Result.success(outputFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseSampleRate(mimeType: String?, defaultRate: Int = 24000): Int {
        if (mimeType.isNullOrBlank()) return defaultRate
        val rateRegex = Regex("""rate=(\d+)""", RegexOption.IGNORE_CASE)
        val match = rateRegex.find(mimeType)
        return match?.groupValues?.get(1)?.toIntOrNull() ?: defaultRate
    }

    private fun isWavOrMp3Header(bytes: ByteArray): Boolean {
        if (bytes.size < 12) return false
        // RIFF....WAVE
        val isRiff = bytes[0] == 'R'.code.toByte() && bytes[1] == 'I'.code.toByte() &&
                bytes[2] == 'F'.code.toByte() && bytes[3] == 'F'.code.toByte() &&
                bytes[8] == 'W'.code.toByte() && bytes[9] == 'A'.code.toByte() &&
                bytes[10] == 'V'.code.toByte() && bytes[11] == 'E'.code.toByte()
        // ID3 header for MP3
        val isId3 = bytes[0] == 'I'.code.toByte() && bytes[1] == 'D'.code.toByte() && bytes[2] == '3'.code.toByte()
        // MP3 frame sync (0xFF and high 3 bits of byte 1 set)
        val isMp3Sync = (bytes[0].toInt() and 0xFF) == 0xFF && (bytes[1].toInt() and 0xE0) == 0xE0

        return isRiff || isId3 || isMp3Sync
    }

    private fun wrapPcmInWav(pcmData: ByteArray, sampleRate: Int, channels: Int, bitsPerSample: Int): ByteArray {
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = (channels * bitsPerSample / 8).toShort()
        val totalDataLen = pcmData.size
        val totalAudioLen = totalDataLen + 36

        val buffer = ByteBuffer.allocate(44 + totalDataLen).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        buffer.put("RIFF".toByteArray())
        buffer.putInt(totalAudioLen)
        buffer.put("WAVE".toByteArray())

        // fmt chunk
        buffer.put("fmt ".toByteArray())
        buffer.putInt(16) // SubChunk1Size for PCM
        buffer.putShort(1) // AudioFormat (1 = PCM)
        buffer.putShort(channels.toShort())
        buffer.putInt(sampleRate)
        buffer.putInt(byteRate)
        buffer.putShort(blockAlign)
        buffer.putShort(bitsPerSample.toShort())

        // data chunk
        buffer.put("data".toByteArray())
        buffer.putInt(totalDataLen)
        buffer.put(pcmData)

        return buffer.array()
    }
}
