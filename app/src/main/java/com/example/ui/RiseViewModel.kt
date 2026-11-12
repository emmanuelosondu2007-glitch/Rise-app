package com.example.ui

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.RiseApplication
import com.example.alarm.AlarmActivity
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.Quote
import com.example.data.model.RewardItem
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyGoal
import com.example.data.repository.RiseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class CelebrationEvent {
    data class LevelUp(val level: Int, val title: String, val xpGained: Int) : CelebrationEvent()
    data class CleanSweep(val bonusCoins: Int, val bonusXp: Int) : CelebrationEvent()
    data class PerfectWeek(val bonusCoins: Int, val bonusXp: Int) : CelebrationEvent()
    data class SundayBounty(val bonusCoins: Int, val bonusXp: Int) : CelebrationEvent()
    data class BadgeUnlocked(val badge: BadgeItem) : CelebrationEvent()
    data class RewardRedeemed(val reward: RewardItem) : CelebrationEvent()
}

class RiseViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as RiseApplication
    val repository = app.repository
    private val soundManager = app.soundManager
    private val vibrationHelper = app.vibrationHelper
    val ttsManager = app.ttsManager
    private val alarmScheduler = app.alarmScheduler

    val todayDateString = repository.getTodayString()
    val currentWeekId = repository.getCurrentWeekId()

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayTasks: StateFlow<List<DailyTask>> = repository.getTasksForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weeklyGoals: StateFlow<List<WeeklyGoal>> = repository.getGoalsForWeek(currentWeekId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val rewards: StateFlow<List<RewardItem>> = repository.allRewards
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<BadgeItem>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryItem>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _quoteOfTheDay = MutableStateFlow<Quote?>(null)
    val quoteOfTheDay: StateFlow<Quote?> = _quoteOfTheDay.asStateFlow()

    private val _activeCelebration = MutableStateFlow<CelebrationEvent?>(null)
    val activeCelebration: StateFlow<CelebrationEvent?> = _activeCelebration.asStateFlow()

    private val _confettiTrigger = MutableStateFlow(0L)
    val confettiTrigger: StateFlow<Long> = _confettiTrigger.asStateFlow()

    private val _showEveningCheckIn = MutableStateFlow(false)
    val showEveningCheckIn: StateFlow<Boolean> = _showEveningCheckIn.asStateFlow()

    private val _showSundayCheckIn = MutableStateFlow(false)
    val showSundayCheckIn: StateFlow<Boolean> = _showSundayCheckIn.asStateFlow()

    private val _showMaxTaskLimitDialog = MutableStateFlow(false)
    val showMaxTaskLimitDialog: StateFlow<Boolean> = _showMaxTaskLimitDialog.asStateFlow()

    private val _weeklyReport = MutableStateFlow<RiseRepository.WeeklyChartReport?>(null)
    val weeklyReport: StateFlow<RiseRepository.WeeklyChartReport?> = _weeklyReport.asStateFlow()

    val friendlyVoices = ttsManager.friendlyVoices

    private val _geminiAudioStatus = MutableStateFlow<String>("")
    val geminiAudioStatus: StateFlow<String> = _geminiAudioStatus.asStateFlow()

    private val _isGeneratingGeminiAudio = MutableStateFlow(false)
    val isGeneratingGeminiAudio: StateFlow<Boolean> = _isGeneratingGeminiAudio.asStateFlow()

    private val _geminiVoicePreviewPlaying = MutableStateFlow(false)
    val geminiVoicePreviewPlaying: StateFlow<Boolean> = _geminiVoicePreviewPlaying.asStateFlow()

    init {
        viewModelScope.launch {
            repository.checkAndRefreshStreakAndShield()
            _quoteOfTheDay.value = repository.getQuoteOfTheDay()
            loadWeeklyReport()
        }
    }

    fun loadWeeklyReport() {
        viewModelScope.launch {
            _weeklyReport.value = repository.getWeeklyProgressReport()
        }
    }

    fun setCategoryFilter(categoryName: String?) {
        _selectedCategoryFilter.value = categoryName
    }

    fun dismissCelebration() {
        _activeCelebration.value = null
    }

    fun dismissMaxTaskLimitDialog() {
        _showMaxTaskLimitDialog.value = false
    }

    fun toggleBusyDayMode() {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val newMode = !current.busyDayMode
            repository.updateProfile(current.copy(busyDayMode = newMode))
            vibrationHelper.vibrateClick()
        }
    }

    fun toggleTask(task: DailyTask) {
        viewModelScope.launch {
            val result = repository.toggleTaskCompletion(task)
            if (result != null) {
                soundManager.playTaskCompleteSound()
                vibrationHelper.vibrateSuccess()
                _confettiTrigger.value = System.currentTimeMillis()
                loadWeeklyReport()

                when {
                    result.didLevelUp -> {
                        soundManager.playLevelUpFanfare()
                        _activeCelebration.value = CelebrationEvent.LevelUp(
                            level = result.newLevel,
                            title = result.newLevelTitle,
                            xpGained = result.xpGained
                        )
                    }
                    result.isPerfectWeek -> {
                        soundManager.playLevelUpFanfare()
                        _activeCelebration.value = CelebrationEvent.PerfectWeek(250, 500)
                    }
                    result.isDailyCleanSweep -> {
                        soundManager.playLevelUpFanfare()
                        _activeCelebration.value = CelebrationEvent.CleanSweep(50, 80)
                    }
                    result.unlockedBadges.isNotEmpty() -> {
                        soundManager.playLevelUpFanfare()
                        _activeCelebration.value = CelebrationEvent.BadgeUnlocked(result.unlockedBadges.first())
                    }
                }
            } else {
                vibrationHelper.vibrateClick()
                loadWeeklyReport()
            }
        }
    }

    /**
     * Adds task with 5-task limit per day (excluding routine items)
     */
    fun addTask(
        title: String,
        notes: String,
        weeklyGoalId: Long?,
        category: CategoryItem,
        priority: Int,
        isEssential: Boolean = false
    ) {
        if (title.isBlank()) return
        val currentCustomCount = todayTasks.value.count { !it.isRoutine }
        if (currentCustomCount >= 5) {
            _showMaxTaskLimitDialog.value = true
            return
        }

        viewModelScope.launch {
            repository.insertTask(
                DailyTask(
                    title = title.trim(),
                    notes = notes.trim(),
                    date = todayDateString,
                    weeklyGoalId = weeklyGoalId,
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryColor = category.colorHex,
                    priority = priority,
                    isRoutine = false,
                    isEssential = isEssential,
                    xpReward = when (priority) { 3 -> 45; 2 -> 35; else -> 25 },
                    coinReward = when (priority) { 3 -> 30; 2 -> 20; else -> 15 }
                )
            )
            soundManager.playCoinSpendSound()
            vibrationHelper.vibrateClick()
            loadWeeklyReport()
        }
    }

    fun deleteTask(task: DailyTask) {
        viewModelScope.launch {
            repository.deleteTask(task)
            vibrationHelper.vibrateClick()
            loadWeeklyReport()
        }
    }

    fun addWeeklyGoal(title: String, targetCount: Int, category: CategoryItem) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertWeeklyGoal(
                WeeklyGoal(
                    title = title.trim(),
                    targetCount = targetCount.coerceAtLeast(1),
                    categoryId = category.id,
                    categoryName = category.name,
                    colorHex = category.colorHex,
                    iconName = category.iconEmoji,
                    weekIdentifier = currentWeekId
                )
            )
            vibrationHelper.vibrateClick()
            loadWeeklyReport()
        }
    }

    fun deleteWeeklyGoal(goal: WeeklyGoal) {
        viewModelScope.launch {
            repository.deleteWeeklyGoal(goal)
            vibrationHelper.vibrateClick()
            loadWeeklyReport()
        }
    }

    fun addCategory(name: String, colorHex: String, iconEmoji: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.insertCategory(
                CategoryItem(
                    name = name.trim(),
                    colorHex = colorHex,
                    iconEmoji = iconEmoji.ifBlank { "🏷️" },
                    isDefault = false
                )
            )
            vibrationHelper.vibrateClick()
        }
    }

    fun updateCategory(category: CategoryItem) {
        viewModelScope.launch {
            repository.updateCategory(category)
            vibrationHelper.vibrateClick()
        }
    }

    fun deleteCategory(category: CategoryItem) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            vibrationHelper.vibrateClick()
        }
    }

    fun redeemReward(reward: RewardItem) {
        viewModelScope.launch {
            val success = repository.redeemReward(reward)
            if (success) {
                soundManager.playLevelUpFanfare()
                vibrationHelper.vibrateSuccess()
                _confettiTrigger.value = System.currentTimeMillis()
                _activeCelebration.value = CelebrationEvent.RewardRedeemed(reward)
            } else {
                vibrationHelper.vibrateClick()
            }
        }
    }

    fun addCustomReward(title: String, description: String, price: Int, emoji: String) {
        if (title.isBlank()) return
        viewModelScope.launch {
            repository.insertReward(
                RewardItem(
                    title = title.trim(),
                    description = description.trim(),
                    coinPrice = price.coerceAtLeast(10),
                    iconEmoji = emoji.ifBlank { "🎁" },
                    isPreset = false
                )
            )
            vibrationHelper.vibrateClick()
        }
    }

    fun deleteReward(reward: RewardItem) {
        viewModelScope.launch {
            repository.deleteReward(reward)
            vibrationHelper.vibrateClick()
        }
    }

    fun updateAlarmTime(hour: Int, minute: Int, enabled: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val updated = current.copy(alarmHour = hour, alarmMinute = minute, alarmEnabled = enabled)
            repository.updateProfile(updated)
            if (enabled) {
                alarmScheduler.scheduleMorningAlarm(hour, minute)
            } else {
                alarmScheduler.cancelMorningAlarm()
            }
            vibrationHelper.vibrateClick()
        }
    }

    fun updateEveningTime(hour: Int, minute: Int, enabled: Boolean) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val updated = current.copy(eveningHour = hour, eveningMinute = minute, eveningReminderEnabled = enabled)
            repository.updateProfile(updated)
            if (enabled) {
                alarmScheduler.scheduleEveningCheckIn(hour, minute)
            }
            vibrationHelper.vibrateClick()
        }
    }

    fun updateSpeechSettings(speed: Float, pitch: Float, voiceName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val updated = current.copy(speechSpeed = speed, speechPitch = pitch, speechVoiceName = voiceName)
            repository.updateProfile(updated)
            ttsManager.applyVoiceSettings(speed, pitch, voiceName)
        }
    }

    fun resetPhoneVoiceToDefault() {
        viewModelScope.launch {
            ttsManager.resetToDefaultVoice()
            val current = userProfile.value ?: return@launch
            val updated = current.copy(
                speechSpeed = com.example.audio.TtsManager.DEFAULT_SPEED,
                speechPitch = com.example.audio.TtsManager.DEFAULT_PITCH,
                speechVoiceName = ""
            )
            repository.updateProfile(updated)
            vibrationHelper.vibrateClick()
        }
    }

    fun previewVoice(voiceName: String, speed: Float, pitch: Float) {
        ttsManager.previewVoice(voiceName, speed, pitch)
    }

    // Gemini Realistic Voice APIs
    fun getGeminiApiKey(): String = app.securePreferences.getGeminiApiKey() ?: ""

    fun hasGeminiApiKey(): Boolean = app.securePreferences.hasGeminiApiKey()

    fun saveGeminiApiKey(key: String) {
        app.securePreferences.saveGeminiApiKey(key.trim())
        _geminiAudioStatus.value = "Gemini key securely saved on device 🔒"
        vibrationHelper.vibrateSuccess()
        generateTomorrowMorningAudio(force = true)
    }

    fun clearGeminiApiKey() {
        app.securePreferences.clearGeminiApiKey()
        _geminiAudioStatus.value = "Gemini API key removed"
        vibrationHelper.vibrateClick()
    }

    fun updateRealisticVoiceSettings(enabled: Boolean, voiceName: String, voiceStyle: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            val updated = current.copy(
                useRealisticVoice = enabled,
                geminiVoiceName = voiceName,
                geminiVoiceStyle = voiceStyle
            )
            repository.updateProfile(updated)
            if (enabled && hasGeminiApiKey()) {
                generateTomorrowMorningAudio(force = true)
            }
            vibrationHelper.vibrateClick()
        }
    }

    fun previewGeminiVoice(voiceName: String, voiceStyle: String) {
        val apiKey = getGeminiApiKey()
        if (apiKey.isBlank()) {
            _geminiAudioStatus.value = "Please enter your Gemini API key first"
            return
        }
        viewModelScope.launch {
            _geminiVoicePreviewPlaying.value = true
            _geminiAudioStatus.value = "Generating $voiceName preview sample..."
            val previewFile = app.geminiTtsService.getPreviewAudioFile()
            val sampleText = "Rise and shine, Emmanuel. This is your realistic AI briefing voice. Today is full of opportunity."
            val result = app.geminiTtsService.generateSpeech(
                apiKey = apiKey,
                text = sampleText,
                voiceName = voiceName,
                voiceStyle = voiceStyle,
                outputFile = previewFile
            )
            if (result.isSuccess) {
                _geminiAudioStatus.value = "Playing $voiceName sample 🔊"
                soundManager.playAudioFile(previewFile) {
                    _geminiVoicePreviewPlaying.value = false
                }
            } else {
                _geminiVoicePreviewPlaying.value = false
                _geminiAudioStatus.value = "Preview failed: ${result.exceptionOrNull()?.message?.take(50)}"
            }
        }
    }

    fun generateTomorrowMorningAudio(force: Boolean = false) {
        viewModelScope.launch {
            val profile = userProfile.value ?: repository.checkAndRefreshStreakAndShield()
            val apiKey = getGeminiApiKey()
            if (!profile.useRealisticVoice) {
                return@launch
            }
            if (apiKey.isBlank()) {
                _geminiAudioStatus.value = "Set your Gemini API key to enable realistic voice"
                return@launch
            }

            val tomorrowStr = repository.getTomorrowString()
            val tomorrowTasks = repository.getTasksForDateSync(tomorrowStr)
            val quote = repository.peekOrGetQuoteForBriefing()

            val script = com.example.audio.TtsManager.buildMorningScript(
                userName = profile.userName,
                quoteText = quote.text,
                quoteAuthor = quote.author,
                todayQuestion = profile.morningWinQuestion,
                tasks = tomorrowTasks.map { it.title }
            )

            _isGeneratingGeminiAudio.value = true
            _geminiAudioStatus.value = "Generating tomorrow's morning briefing audio with ${profile.geminiVoiceName}..."

            val morningFile = app.geminiTtsService.getMorningAudioFile()
            val result = app.geminiTtsService.generateSpeech(
                apiKey = apiKey,
                text = script,
                voiceName = profile.geminiVoiceName,
                voiceStyle = profile.geminiVoiceStyle,
                outputFile = morningFile
            )

            _isGeneratingGeminiAudio.value = false
            if (result.isSuccess) {
                _geminiAudioStatus.value = "Tomorrow's briefing ready (${morningFile.length() / 1024} KB) ✅"
                val updatedProfile = profile.copy(lastMorningAudioGeneratedForDate = tomorrowStr)
                repository.updateProfile(updatedProfile)
            } else {
                _geminiAudioStatus.value = "Audio generation error: ${result.exceptionOrNull()?.message?.take(60)}"
            }
        }
    }

    fun addTomorrowTask(title: String, category: CategoryItem, priority: Int = 2) {
        if (title.isBlank()) return
        val tomorrow = repository.getTomorrowString()
        viewModelScope.launch {
            repository.insertTask(
                DailyTask(
                    title = title.trim(),
                    notes = "Planned the night before",
                    date = tomorrow,
                    categoryId = category.id,
                    categoryName = category.name,
                    categoryColor = category.colorHex,
                    priority = priority,
                    isRoutine = false,
                    isEssential = false,
                    xpReward = when (priority) { 3 -> 45; 2 -> 35; else -> 25 },
                    coinReward = when (priority) { 3 -> 30; 2 -> 20; else -> 15 }
                )
            )
            soundManager.playCoinSpendSound()
            vibrationHelper.vibrateClick()
            // Regenerate tomorrow morning audio automatically
            generateTomorrowMorningAudio(force = true)
        }
    }

    fun updateUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val current = userProfile.value ?: return@launch
            repository.updateProfile(current.copy(userName = name.trim()))
            vibrationHelper.vibrateClick()
            generateTomorrowMorningAudio(force = true)
        }
    }

    fun openEveningCheckIn() {
        _showEveningCheckIn.value = true
        speakEveningReflection()
    }

    fun closeEveningCheckIn() {
        _showEveningCheckIn.value = false
        soundManager.stopAudioPlayback()
        ttsManager.stop()
        // Generate tomorrow morning audio right when evening check-in wraps up
        generateTomorrowMorningAudio(force = true)
    }

    fun openSundayCheckIn() {
        loadWeeklyReport()
        _showSundayCheckIn.value = true
    }

    fun closeSundayCheckIn() {
        _showSundayCheckIn.value = false
    }

    fun claimSundayCheckInBounty() {
        viewModelScope.launch {
            val (bonusXp, bonusCoins) = repository.completeSundayCheckIn()
            soundManager.playLevelUpFanfare()
            vibrationHelper.vibrateSuccess()
            _confettiTrigger.value = System.currentTimeMillis()
            _activeCelebration.value = CelebrationEvent.SundayBounty(bonusCoins, bonusXp)
            _showSundayCheckIn.value = false
        }
    }

    fun speakEveningReflection() {
        val profile = userProfile.value ?: return
        val tasks = todayTasks.value
        val completedCount = tasks.count { it.isCompleted }
        val xpEarned = tasks.filter { it.isCompleted }.sumOf { it.xpReward }

        val script = com.example.audio.TtsManager.buildEveningScript(
            userName = profile.userName,
            completedCount = completedCount,
            totalCount = tasks.size,
            xpEarned = xpEarned,
            streakDays = profile.currentStreak
        )

        val apiKey = getGeminiApiKey()
        if (profile.useRealisticVoice && apiKey.isNotBlank()) {
            viewModelScope.launch {
                val eveningFile = app.geminiTtsService.getEveningAudioFile()
                val result = app.geminiTtsService.generateSpeech(
                    apiKey = apiKey,
                    text = script,
                    voiceName = profile.geminiVoiceName,
                    voiceStyle = profile.geminiVoiceStyle,
                    outputFile = eveningFile
                )
                if (result.isSuccess) {
                    soundManager.playAudioFile(eveningFile)
                } else {
                    // Fall back to phone built-in TTS
                    ttsManager.speakEveningReflection(
                        userName = profile.userName,
                        completedCount = completedCount,
                        totalCount = tasks.size,
                        xpEarned = xpEarned,
                        streakDays = profile.currentStreak
                    )
                }
            }
        } else {
            ttsManager.speakEveningReflection(
                userName = profile.userName,
                completedCount = completedCount,
                totalCount = tasks.size,
                xpEarned = xpEarned,
                streakDays = profile.currentStreak
            )
        }
    }

    fun speakMorningBriefing() {
        val profile = userProfile.value ?: return
        val quote = quoteOfTheDay.value ?: return
        val tasks = todayTasks.value.map { it.title }

        val morningFile = app.geminiTtsService.getMorningAudioFile()
        if (profile.useRealisticVoice && morningFile.exists() && morningFile.length() > 0) {
            val played = soundManager.playAudioFile(morningFile)
            if (!played) {
                ttsManager.speakMorningBriefing(
                    userName = profile.userName,
                    quoteText = quote.text,
                    quoteAuthor = quote.author,
                    todayQuestion = profile.morningWinQuestion,
                    tasks = tasks
                )
            }
        } else {
            ttsManager.speakMorningBriefing(
                userName = profile.userName,
                quoteText = quote.text,
                quoteAuthor = quote.author,
                todayQuestion = profile.morningWinQuestion,
                tasks = tasks
            )
        }
    }

    fun testAlarmNow() {
        val intent = Intent(app, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        app.startActivity(intent)
    }
}
