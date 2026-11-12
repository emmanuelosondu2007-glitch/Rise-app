package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.Quote
import com.example.data.model.RewardItem
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyGoal
import com.example.data.quotes.QuoteBank
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        CategoryItem::class,
        DailyTask::class,
        WeeklyGoal::class,
        RewardItem::class,
        UserProfile::class,
        BadgeItem::class,
        Quote::class
    ],
    version = 2,
    exportSchema = false
)
abstract class RiseDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao
    abstract fun weeklyGoalDao(): WeeklyGoalDao
    abstract fun rewardDao(): RewardDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun badgeDao(): BadgeDao
    abstract fun quoteDao(): QuoteDao

    companion object {
        @Volatile
        private var INSTANCE: RiseDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RiseDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RiseDatabase::class.java,
                    "rise_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(RiseDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class RiseDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: RiseDatabase) {
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val cal = Calendar.getInstance()
            val weekId = "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"

            // 1. User Profile
            database.userProfileDao().insertUserProfile(
                UserProfile(
                    id = 1,
                    userName = "Emmanuel",
                    xp = 50,
                    level = 1,
                    levelTitle = "Starter",
                    coins = 120,
                    currentStreak = 1,
                    highestStreak = 1,
                    listCompletionStreak = 0,
                    highestListCompletionStreak = 0,
                    lastActiveDate = todayStr,
                    streakShieldAvailable = true,
                    lastShieldResetWeek = weekId,
                    busyDayMode = false,
                    alarmHour = 7,
                    alarmMinute = 0,
                    alarmEnabled = true,
                    eveningHour = 20,
                    eveningMinute = 30,
                    eveningReminderEnabled = true,
                    sundayReminderHour = 21,
                    sundayReminderMinute = 0,
                    sundayReminderEnabled = true,
                    soundEnabled = true,
                    ttsVoiceEnabled = true,
                    speechSpeed = 0.95f,
                    speechPitch = 0.85f, // Deep, clear voice
                    morningWinQuestion = "What is the one thing that would make today a win?",
                    totalTasksCompleted = 0,
                    perfectDaysCount = 0
                )
            )

            // 2. Pre-load Categories (Spiritual, Law, Mentorship, Editing/Bezalim, Fellowship, Personal)
            val defaultCategories = listOf(
                CategoryItem(name = "Spiritual", colorHex = "#8B5CF6", iconEmoji = "✨", description = "devotion, prayer, sermons", isDefault = true),
                CategoryItem(name = "Law", colorHex = "#3B82F6", iconEmoji = "⚖️", description = "lectures, study, assignments", isDefault = true),
                CategoryItem(name = "Mentorship", colorHex = "#10B981", iconEmoji = "🌱", description = "daily report, book reading, voice notes", isDefault = true),
                CategoryItem(name = "Editing / Bezalim", colorHex = "#F59E0B", iconEmoji = "🎬", description = "clipping, video projects", isDefault = true),
                CategoryItem(name = "Fellowship", colorHex = "#EC4899", iconEmoji = "🤝", description = "tech unit, meetings", isDefault = true),
                CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿", description = "health, rest, family", isDefault = true)
            )
            database.categoryDao().insertAllCategories(defaultCategories)

            // 3. Pre-load Daily Routine (recurring every day)
            val routineTasks = listOf(
                DailyTask(
                    title = "On waking: Devotion and prayer, write one line of what I learned",
                    notes = "Spiritual foundation for the day",
                    date = todayStr,
                    isCompleted = false,
                    xpReward = 35,
                    coinReward = 20,
                    categoryName = "Spiritual",
                    categoryColor = "#8B5CF6",
                    isRoutine = true,
                    isEssential = true,
                    priority = 3
                ),
                DailyTask(
                    title = "After lectures: Read the two write-ups and note what I learned",
                    notes = "Mentorship growth write-ups",
                    date = todayStr,
                    isCompleted = false,
                    xpReward = 30,
                    coinReward = 20,
                    categoryName = "Mentorship",
                    categoryColor = "#10B981",
                    isRoutine = true,
                    isEssential = true,
                    priority = 2
                ),
                DailyTask(
                    title = "Evening: Read 10 pages of my assigned book",
                    notes = "Consistent daily reading habit",
                    date = todayStr,
                    isCompleted = false,
                    xpReward = 30,
                    coinReward = 20,
                    categoryName = "Mentorship",
                    categoryColor = "#10B981",
                    isRoutine = true,
                    isEssential = false,
                    priority = 2
                ),
                DailyTask(
                    title = "Night: Submit my daily report before 11:30pm (reminder at 10:30pm)",
                    notes = "Daily accountability submission",
                    date = todayStr,
                    isCompleted = false,
                    xpReward = 40,
                    coinReward = 25,
                    categoryName = "Mentorship",
                    categoryColor = "#10B981",
                    isRoutine = true,
                    isEssential = true,
                    priority = 3
                )
            )
            database.taskDao().insertAllTasks(routineTasks)

            // 4. Pre-load Weekly Tasks for this week: Tuesday, Friday, Saturday
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            fun getDateForDayOfWeek(targetDay: Int): String {
                val c = Calendar.getInstance()
                c.set(Calendar.DAY_OF_WEEK, targetDay)
                return sdf.format(c.time)
            }

            val tuesdayDate = getDateForDayOfWeek(Calendar.TUESDAY)
            val fridayDate = getDateForDayOfWeek(Calendar.FRIDAY)
            val saturdayDate = getDateForDayOfWeek(Calendar.SATURDAY)

            database.taskDao().insertTask(
                DailyTask(
                    title = "Tuesday: Listen to Sermon 1 and write what I learned",
                    notes = "Spiritual sermon reflection",
                    date = tuesdayDate,
                    isCompleted = false,
                    xpReward = 45,
                    coinReward = 30,
                    categoryName = "Spiritual",
                    categoryColor = "#8B5CF6",
                    scheduledDayOfWeek = Calendar.TUESDAY,
                    isRoutine = false,
                    isEssential = true,
                    priority = 3
                )
            )
            database.taskDao().insertTask(
                DailyTask(
                    title = "Friday: Listen to Sermon 2 and write what I learned",
                    notes = "Spiritual sermon reflection",
                    date = fridayDate,
                    isCompleted = false,
                    xpReward = 45,
                    coinReward = 30,
                    categoryName = "Spiritual",
                    categoryColor = "#8B5CF6",
                    scheduledDayOfWeek = Calendar.FRIDAY,
                    isRoutine = false,
                    isEssential = true,
                    priority = 3
                )
            )
            database.taskDao().insertTask(
                DailyTask(
                    title = "Saturday: Record my weekly book voice note before 9pm",
                    notes = "Mentorship weekly audio summary",
                    date = saturdayDate,
                    isCompleted = false,
                    xpReward = 50,
                    coinReward = 35,
                    categoryName = "Mentorship",
                    categoryColor = "#10B981",
                    scheduledDayOfWeek = Calendar.SATURDAY,
                    isRoutine = false,
                    isEssential = true,
                    priority = 3
                )
            )

            // 5. Pre-load Badges
            val initialBadges = listOf(
                BadgeItem(code = "FIRST_VICTORY", title = "First Victory", description = "Conquer your very first task", iconEmoji = "🎯", xpBonus = 50),
                BadgeItem(code = "STREAK_3", title = "Sparks Ignited", description = "Hold a 3-day winning streak", iconEmoji = "🔥", xpBonus = 80),
                BadgeItem(code = "STREAK_7", title = "Blazing Unstoppable", description = "Reach a 7-day daily streak", iconEmoji = "⚡", xpBonus = 150),
                BadgeItem(code = "STREAK_30", title = "Iron Will", description = "Maintain a monumental 30-day streak", iconEmoji = "👑", xpBonus = 500),
                BadgeItem(code = "LIST_MASTER_3", title = "Habit Formed", description = "Completed 100% of your daily list 3 days in a row", iconEmoji = "📋", xpBonus = 100),
                BadgeItem(code = "LIST_MASTER_7", title = "To-Do List Person", description = "Completed your list 7 consecutive days!", iconEmoji = "🏆", xpBonus = 250),
                BadgeItem(code = "CLEAN_SWEEP", title = "Daily Clean Sweep", description = "Complete 100% of tasks in one day", iconEmoji = "✨", xpBonus = 100),
                BadgeItem(code = "PERFECT_WEEK", title = "Rise Ascendant", description = "Achieve all weekly goals in a single week", iconEmoji = "🌟", xpBonus = 350),
                BadgeItem(code = "SUNDAY_REVIEWER", title = "Strategic Planner", description = "Completed Sunday night check-in & weekly prep", iconEmoji = "🧭", xpBonus = 200),
                BadgeItem(code = "REWARD_CLAIMED", title = "Sweet Taste of Victory", description = "Redeem your first reward in the shop", iconEmoji = "🎁", xpBonus = 60),
                BadgeItem(code = "EARLY_RISER", title = "Dawn Warrior", description = "Rise to the morning alarm 3 times", iconEmoji = "🌅", xpBonus = 120)
            )
            database.badgeDao().insertAll(initialBadges)

            // 6. 365+ Quotes
            database.quoteDao().insertAll(QuoteBank.quotes)
        }
    }
}
