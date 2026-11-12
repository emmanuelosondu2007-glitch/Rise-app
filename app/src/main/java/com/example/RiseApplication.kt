package com.example

import android.app.Application
import com.example.alarm.AlarmScheduler
import com.example.audio.SoundManager
import com.example.audio.TtsManager
import com.example.audio.VibrationHelper
import com.example.data.db.RiseDatabase
import com.example.data.repository.RiseRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RiseApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { RiseDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { RiseRepository(database) }
    val soundManager by lazy { SoundManager(this) }
    val vibrationHelper by lazy { VibrationHelper(this) }
    val ttsManager by lazy { TtsManager(this) }
    val alarmScheduler by lazy { AlarmScheduler(this) }
    val securePreferences by lazy { com.example.data.security.SecurePreferences(this) }
    val geminiTtsService by lazy { com.example.audio.GeminiTtsService(this) }

    override fun onCreate() {
        super.onCreate()
        // Ensure database is initialized & streaks checked
        applicationScope.launch {
            repository.checkAndRefreshStreakAndShield()
        }
        alarmScheduler.scheduleNightPreGeneration(23, 0)
    }

    override fun onTerminate() {
        super.onTerminate()
        ttsManager.shutdown()
        soundManager.stopAlarmRingtone()
    }
}
