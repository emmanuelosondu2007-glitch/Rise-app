package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _availableVoices = MutableStateFlow<List<Voice>>(emptyList())
    val availableVoices: StateFlow<List<Voice>> = _availableVoices.asStateFlow()

    private var currentSpeed: Float = 0.95f
    private var currentPitch: Float = 0.85f // Deep, resonant adult pitch default
    private var currentVoiceName: String? = null

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true

                    // Route TTS through ALARM audio stream so it is heard loud & clear even if media volume is low
                    val audioAttributes = AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                        .build()
                    engine.setAudioAttributes(audioAttributes)

                    engine.setSpeechRate(currentSpeed)
                    engine.setPitch(currentPitch)

                    // Load available voices
                    try {
                        val voices = engine.voices
                        if (voices != null) {
                            _availableVoices.value = voices.toList().sortedBy { it.name }
                        }
                    } catch (ignored: Exception) {}

                    // Apply default deep clear voice if specified
                    currentVoiceName?.let { vName ->
                        val matched = engine.voices?.find { it.name == vName }
                        if (matched != null) {
                            engine.voice = matched
                        }
                    }

                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                }
            }
        }
    }

    fun applyVoiceSettings(speed: Float, pitch: Float, voiceName: String?) {
        currentSpeed = speed
        currentPitch = pitch
        currentVoiceName = voiceName

        tts?.let { engine ->
            engine.setSpeechRate(speed)
            engine.setPitch(pitch)
            if (!voiceName.isNullOrBlank()) {
                val matched = engine.voices?.find { it.name == voiceName }
                if (matched != null) {
                    engine.voice = matched
                }
            }
        }
    }

    fun previewVoice(voiceName: String?, speed: Float, pitch: Float) {
        tts?.let { engine ->
            engine.setSpeechRate(speed)
            engine.setPitch(pitch)
            if (!voiceName.isNullOrBlank()) {
                val matched = engine.voices?.find { it.name == voiceName }
                if (matched != null) {
                    engine.voice = matched
                }
            }
            speak("Rise and shine, Emmanuel. This is your morning briefing voice.")
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (!isInitialized || tts == null) return
        val params = android.os.Bundle()
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "rise_speech_${System.currentTimeMillis()}")
        tts?.speak(text, queueMode, params, "rise_speech_${System.currentTimeMillis()}")
    }

    fun speakMorningBriefing(
        userName: String,
        quoteText: String,
        quoteAuthor: String,
        todayQuestion: String,
        tasks: List<String>
    ) {
        val sb = StringBuilder()
        sb.append("Good morning $userName. Rise and shine. ")
        sb.append("Here is your quote of the day. \"$quoteText\" by $quoteAuthor. ")

        if (todayQuestion.isNotBlank()) {
            sb.append("Today's focus question: $todayQuestion. ")
        }

        if (tasks.isEmpty()) {
            sb.append("You have no tasks scheduled yet. Open Rise to set your targets.")
        } else {
            val countWord = if (tasks.size == 1) "task" else "tasks"
            sb.append("Today you have ${tasks.size} $countWord on your battle plan. ")
            val ordinals = listOf("First", "Second", "Third", "Fourth", "Fifth", "Sixth", "Seventh", "Eighth", "Ninth", "Tenth")
            tasks.take(5).forEachIndexed { index, taskTitle ->
                val ordinal = ordinals.getOrElse(index) { "Next" }
                sb.append("$ordinal, $taskTitle. ")
            }
            sb.append("Make today count!")
        }

        speak(sb.toString())
    }

    fun speakEveningReflection(
        userName: String,
        completedCount: Int,
        totalCount: Int,
        xpEarned: Int,
        streakDays: Int
    ) {
        val sb = StringBuilder()
        sb.append("Good evening $userName. ")
        if (completedCount == totalCount && totalCount > 0) {
            sb.append("Incredible work! You achieved a perfect score today, completing all $totalCount of your goals, and earning $xpEarned XP. ")
        } else if (completedCount > 0) {
            sb.append("Great effort today! You conquered $completedCount out of $totalCount goals, earning $xpEarned XP. ")
        } else {
            sb.append("Every day is a stepping stone. Tomorrow is another chance to rise and win. ")
        }
        sb.append("Your winning streak is $streakDays days. Rest well, champion.")
        speak(sb.toString())
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
