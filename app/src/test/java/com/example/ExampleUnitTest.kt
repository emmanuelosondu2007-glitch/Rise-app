package com.example

import com.example.audio.GeminiTtsService
import com.example.audio.TtsManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun geminiVoices_containsPrebuiltOptions() {
        val voices = GeminiTtsService.PREBUILT_VOICES
        assertEquals(13, voices.size)
        val names = voices.map { it.name }
        assertTrue(names.contains("Charon"))
        assertTrue(names.contains("Gacrux"))
        assertTrue(names.contains("Sadaltager"))
        assertTrue(names.contains("Algenib"))
        assertTrue(names.contains("Orus"))
        assertTrue(names.contains("Iapetus"))
        assertTrue(names.contains("Rasalgethi"))
        assertTrue(names.contains("Sulafat"))
        assertTrue(names.contains("Achird"))
        assertTrue(names.contains("Fenrir"))
        assertTrue(names.contains("Puck"))
        assertTrue(names.contains("Kore"))
        assertTrue(names.contains("Aoede"))
    }

    @Test
    fun morningScript_containsRequiredElements() {
        val script = TtsManager.buildMorningScript(
            userName = "Emmanuel",
            quoteText = "Commit your work to the Lord",
            quoteAuthor = "Proverbs 16:3",
            todayQuestion = "What would make today a win?",
            tasks = listOf("Complete devotion", "Read 10 pages")
        )

        assertTrue(script.contains("Good morning Emmanuel"))
        assertTrue(script.contains("Commit your work to the Lord"))
        assertTrue(script.contains("What would make today a win?"))
        assertTrue(script.contains("First, Complete devotion"))
        assertTrue(script.contains("Second, Read 10 pages"))
    }

    @Test
    fun eveningScript_containsScoreAndStreak() {
        val script = TtsManager.buildEveningScript(
            userName = "Emmanuel",
            completedCount = 4,
            totalCount = 4,
            xpEarned = 140,
            streakDays = 5
        )

        assertTrue(script.contains("Good evening Emmanuel"))
        assertTrue(script.contains("perfect score"))
        assertTrue(script.contains("140 XP"))
        assertTrue(script.contains("5 days"))
    }
}
