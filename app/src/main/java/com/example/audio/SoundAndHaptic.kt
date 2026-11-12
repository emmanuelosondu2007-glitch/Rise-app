package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

class SoundManager(private val context: Context) {

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private var mediaPlayer: MediaPlayer? = null
    private var isMuted = false

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    /**
     * Start playing a real, loud alarm ringtone on the ALARM audio stream at MAXIMUM volume
     */
    fun startAlarmRingtone() {
        stopAlarmRingtone()
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                am.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
            }

            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

            val mp = MediaPlayer()
            mp.setDataSource(context, alarmUri)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            mp.setAudioAttributes(audioAttributes)
            mp.isLooping = true
            mp.prepare()
            mp.start()
            mediaPlayer = mp
        } catch (e: Exception) {
            // Fallback synthesized tone loop if device audio provider blocks Uri
            startFallbackAlarmLoop()
        }
    }

    private var fallbackJob: Job? = null
    private fun startFallbackAlarmLoop() {
        fallbackJob?.cancel()
        fallbackJob = audioScope.launch {
            while (true) {
                playTone(880f, 250, 1.0f)
                delay(120)
                playTone(1174f, 350, 1.0f)
                delay(300)
            }
        }
    }

    fun stopAlarmRingtone() {
        fallbackJob?.cancel()
        fallbackJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (ignored: Exception) {}
        mediaPlayer = null
    }

    private var audioFilePlayer: MediaPlayer? = null

    /**
     * Plays a generated AI voice audio file on the ALARM stream at maximum volume
     */
    fun playAudioFile(file: java.io.File, onComplete: (() -> Unit)? = null): Boolean {
        stopAudioPlayback()
        if (!file.exists() || file.length() == 0L) {
            return false
        }
        return try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
            audioManager?.let { am ->
                val maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_ALARM)
                am.setStreamVolume(AudioManager.STREAM_ALARM, maxVolume, 0)
            }

            val mp = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setAudioAttributes(audioAttributes)
                setOnCompletionListener {
                    stopAudioPlayback()
                    onComplete?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    stopAudioPlayback()
                    onComplete?.invoke()
                    true
                }
                prepare()
                start()
            }
            audioFilePlayer = mp
            true
        } catch (e: Exception) {
            stopAudioPlayback()
            false
        }
    }

    /**
     * Plays preview audio on the normal MEDIA stream at current media volume
     */
    fun playPreviewMediaAudio(file: java.io.File, onComplete: (() -> Unit)? = null): Boolean {
        stopAudioPlayback()
        if (!file.exists() || file.length() == 0L) {
            return false
        }
        return try {
            val mp = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                setAudioAttributes(audioAttributes)
                setOnCompletionListener {
                    stopAudioPlayback()
                    onComplete?.invoke()
                }
                setOnErrorListener { _, _, _ ->
                    stopAudioPlayback()
                    onComplete?.invoke()
                    true
                }
                prepare()
                start()
            }
            audioFilePlayer = mp
            true
        } catch (e: Exception) {
            stopAudioPlayback()
            false
        }
    }

    fun stopAudioPlayback() {
        try {
            audioFilePlayer?.stop()
            audioFilePlayer?.release()
        } catch (ignored: Exception) {}
        audioFilePlayer = null
    }

    fun isPlayingAudioFile(): Boolean {
        return try {
            audioFilePlayer?.isPlaying == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Task complete victory chime
     */
    fun playTaskCompleteSound() {
        if (isMuted) return
        audioScope.launch {
            playTone(1318.5f, 90, 0.7f)
            delay(80)
            playTone(1661.2f, 90, 0.75f)
            delay(80)
            playTone(1975.5f, 100, 0.8f)
            delay(90)
            playTone(2637.0f, 180, 0.9f)
        }
    }

    /**
     * Coin spend / reward purchase
     */
    fun playCoinSpendSound() {
        if (isMuted) return
        audioScope.launch {
            playTone(2093.0f, 70, 0.6f)
            delay(60)
            playTone(3135.9f, 120, 0.7f)
        }
    }

    /**
     * Fanfare for level up or Sunday review bounty
     */
    fun playLevelUpFanfare() {
        if (isMuted) return
        audioScope.launch {
            playTone(523.25f, 110, 0.8f)
            delay(100)
            playTone(659.25f, 110, 0.85f)
            delay(100)
            playTone(783.99f, 110, 0.9f)
            delay(100)
            playTone(1046.50f, 350, 1.0f)
        }
    }

    fun playQuoteRevealChime() {
        if (isMuted) return
        audioScope.launch {
            playTone(880.0f, 180, 0.5f)
            delay(160)
            playTone(1174.66f, 300, 0.6f)
        }
    }

    /**
     * Soft, warm chime for Ritual Mode step completion
     */
    fun playGentleRitualCompleteSound() {
        if (isMuted) return
        audioScope.launch {
            playGentleMediaTone(523.25f, 140, 0.4f) // C5
            delay(110)
            playGentleMediaTone(659.25f, 160, 0.45f) // E5
            delay(120)
            playGentleMediaTone(783.99f, 260, 0.5f) // G5
        }
    }

    private fun playGentleMediaTone(freqHz: Float, durationMs: Int, volume: Float = 0.45f) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val generatedSnd = ByteArray(2 * numSamples)
            var idx = 0

            for (i in 0 until numSamples) {
                val attack = (i / 400.0).coerceAtMost(1.0)
                val release = ((numSamples - i).toDouble() / (numSamples * 0.65)).coerceIn(0.0, 1.0)
                val envelope = attack * release
                val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
                val sampleVal = (sin(angle) * 32767 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()

                generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
                generatedSnd[idx++] = ((sampleVal.toInt() and 0xff00) ushr 8).toByte()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                generatedSnd.size,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            track.write(generatedSnd, 0, generatedSnd.size)
            track.play()
            track.setNotificationMarkerPosition(numSamples)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    t?.release()
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (ignored: Exception) {}
    }

    private fun playTone(freqHz: Float, durationMs: Int, volume: Float = 0.8f) {
        try {
            val sampleRate = 44100
            val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
            val generatedSnd = ByteArray(2 * numSamples)
            var idx = 0

            for (i in 0 until numSamples) {
                val envelope = when {
                    i < 200 -> (i / 200.0)
                    i > numSamples - 300 -> ((numSamples - i) / 300.0)
                    else -> 1.0
                }
                val angle = 2.0 * Math.PI * i / (sampleRate / freqHz)
                val sampleVal = (sin(angle) * 32767 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()

                generatedSnd[idx++] = (sampleVal.toInt() and 0x00ff).toByte()
                generatedSnd[idx++] = ((sampleVal.toInt() and 0xff00) ushr 8).toByte()
            }

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                audioAttributes,
                audioFormat,
                generatedSnd.size,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )

            track.write(generatedSnd, 0, generatedSnd.size)
            track.play()
            track.setNotificationMarkerPosition(numSamples)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    t?.release()
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (e: Exception) {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 80)
                toneGen.startTone(ToneGenerator.TONE_PROP_BEEP, durationMs)
            } catch (ignored: Exception) {}
        }
    }
}

class VibrationHelper(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        manager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    fun vibrateClick() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(25)
            }
        } catch (ignored: Exception) {}
    }

    fun vibrateSuccess() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 70, 60, 110)
                val amplitudes = intArrayOf(0, 180, 0, 255)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 70, 60, 110), -1)
            }
        } catch (ignored: Exception) {}
    }

    fun vibrateAlarm() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 600, 200, 600, 200)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(longArrayOf(0, 600, 200, 600, 200), 0)
            }
        } catch (ignored: Exception) {}
    }

    fun stopVibration() {
        try {
            vibrator?.cancel()
        } catch (ignored: Exception) {}
    }
}
