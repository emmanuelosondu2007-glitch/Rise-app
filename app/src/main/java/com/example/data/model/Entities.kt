package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val colorHex: String,
    val iconEmoji: String,
    val description: String = "",
    val isDefault: Boolean = false
)

@Entity(tableName = "daily_tasks")
data class DailyTask(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val notes: String = "",
    val date: String, // "yyyy-MM-dd"
    val isCompleted: Boolean = false,
    val xpReward: Int = 30,
    val coinReward: Int = 20,
    val weeklyGoalId: Long? = null,
    val categoryId: Long? = null,
    val categoryName: String = "Personal",
    val categoryColor: String = "#06B6D4",
    val isRoutine: Boolean = false, // Daily recurring routine (doesn't count to 5-task limit)
    val isMovedFromPreviousDay: Boolean = false, // Rolled over from yesterday without penalty
    val isEssential: Boolean = false, // Minimum task shown in Busy Day mode
    val scheduledDayOfWeek: Int? = null, // e.g. Calendar.TUESDAY, Calendar.FRIDAY, Calendar.SATURDAY
    val priority: Int = 1,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

@Entity(tableName = "weekly_goals")
data class WeeklyGoal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetCount: Int = 5,
    val categoryId: Long? = null,
    val categoryName: String = "Personal",
    val iconName: String = "flag",
    val colorHex: String = "#FFD700",
    val weekIdentifier: String, // "yyyy-Www"
    val isArchived: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "rewards")
data class RewardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val coinPrice: Int,
    val iconEmoji: String = "🎁",
    val isPreset: Boolean = false,
    val timesClaimed: Int = 0,
    val lastClaimedAt: Long? = null
)

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1,
    val userName: String = "Emmanuel",
    val xp: Int = 0,
    val level: Int = 1,
    val levelTitle: String = "Starter",
    val coins: Int = 120,
    val currentStreak: Int = 1,
    val highestStreak: Int = 1,
    val listCompletionStreak: Int = 0, // Habit tracker: consecutive days 100% of tasks completed
    val highestListCompletionStreak: Int = 0,
    val lastActiveDate: String = "",
    val streakShieldAvailable: Boolean = true,
    val lastShieldResetWeek: String = "",
    val busyDayMode: Boolean = false, // Shows only essential/routine tasks
    val alarmHour: Int = 7,
    val alarmMinute: Int = 0,
    val alarmEnabled: Boolean = true,
    val eveningHour: Int = 20,
    val eveningMinute: Int = 30,
    val eveningReminderEnabled: Boolean = true,
    val sundayReminderHour: Int = 21, // 9:00 PM Sunday
    val sundayReminderMinute: Int = 0,
    val sundayReminderEnabled: Boolean = true,
    val lastSundayCheckInWeek: String = "",
    val soundEnabled: Boolean = true,
    val ttsVoiceEnabled: Boolean = true,
    val speechSpeed: Float = 0.95f,
    val speechPitch: Float = 0.85f, // Deep, clear adult voice default
    val speechVoiceName: String = "",
    val morningWinQuestion: String = "What is the one thing that would make today a win?",
    val morningWinAnswer: String = "",
    val useRealisticVoice: Boolean = false,
    val geminiVoiceName: String = "Charon",
    val geminiVoiceStyle: String = "Warm, deep, unhurried storyteller. Speaks like a wise, encouraging mentor, with natural pauses and a slight smile in the voice.",
    val lastMorningAudioGeneratedForDate: String = "",
    val totalTasksCompleted: Int = 0,
    val perfectDaysCount: Int = 0,
    val perfectWeeksCount: Int = 0
)

@Entity(tableName = "badges")
data class BadgeItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val title: String,
    val description: String,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long? = null,
    val xpBonus: Int = 50
)

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val author: String,
    val category: String,
    val isBibleVerse: Boolean = false,
    val wasUsed: Boolean = false,
    val orderIndex: Int = 0
)
