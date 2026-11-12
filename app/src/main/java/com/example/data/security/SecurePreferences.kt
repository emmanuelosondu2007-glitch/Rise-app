package com.example.data.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecurePreferences(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "rise_secure_prefs"
        private const val KEY_ENCRYPTED_API_KEY = "enc_gemini_api_key"
        private const val KEY_IV = "enc_gemini_iv"
        private const val KEY_ALIAS = "RiseGeminiKeyAlias"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val keyGenSpec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(keyGenSpec)
            return keyGenerator.generateKey()
        }
        val entry = keyStore.getEntry(KEY_ALIAS, null) as KeyStore.SecretKeyEntry
        return entry.secretKey
    }

    fun saveGeminiApiKey(apiKey: String) {
        if (apiKey.isBlank()) {
            clearGeminiApiKey()
            return
        }
        try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            val iv = cipher.iv
            val encryptedBytes = cipher.doFinal(apiKey.trim().toByteArray(Charsets.UTF_8))

            prefs.edit()
                .putString(KEY_ENCRYPTED_API_KEY, Base64.encodeToString(encryptedBytes, Base64.NO_WRAP))
                .putString(KEY_IV, Base64.encodeToString(iv, Base64.NO_WRAP))
                .apply()
        } catch (e: Exception) {
            // Fallback obfuscated storage if Keystore hardware is unavailable in container/emulator
            val fallback = Base64.encodeToString(apiKey.trim().toByteArray(Charsets.UTF_8), Base64.NO_WRAP)
            prefs.edit().putString(KEY_ENCRYPTED_API_KEY, fallback).putString(KEY_IV, "plain_b64").apply()
        }
    }

    fun getGeminiApiKey(): String? {
        val encStr = prefs.getString(KEY_ENCRYPTED_API_KEY, null) ?: return null
        val ivStr = prefs.getString(KEY_IV, null) ?: return null
        if (ivStr == "plain_b64") {
            return String(Base64.decode(encStr, Base64.NO_WRAP), Charsets.UTF_8)
        }
        return try {
            val secretKey = getOrCreateSecretKey()
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val iv = Base64.decode(ivStr, Base64.NO_WRAP)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
            val decryptedBytes = cipher.doFinal(Base64.decode(encStr, Base64.NO_WRAP))
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            null
        }
    }

    fun hasGeminiApiKey(): Boolean {
        return !getGeminiApiKey().isNullOrBlank()
    }

    fun clearGeminiApiKey() {
        prefs.edit().remove(KEY_ENCRYPTED_API_KEY).remove(KEY_IV).apply()
    }

    fun getGeminiDailyUsageCount(dateStr: String): Int {
        return prefs.getInt("gemini_usage_$dateStr", 0)
    }

    fun incrementGeminiDailyUsage(dateStr: String): Int {
        val current = getGeminiDailyUsageCount(dateStr)
        val next = current + 1
        prefs.edit().putInt("gemini_usage_$dateStr", next).apply()
        return next
    }
}
