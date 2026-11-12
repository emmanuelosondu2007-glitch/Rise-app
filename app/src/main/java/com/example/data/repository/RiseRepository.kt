package com.example.data.repository

import com.example.data.db.RiseDatabase
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.Quote
import com.example.data.model.RewardItem
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyGoal
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RiseRepository(private val db: RiseDatabase) {

    val userProfile: Flow<UserProfile?> = db.userProfileDao().getUserProfileFlow()
    val allBadges: Flow<List<BadgeItem>> = db.badgeDao().getAllBadges()
    val allRewards: Flow<List<RewardItem>> = db.rewardDao().getAllRewards()
    val allCategories: Flow<List<CategoryItem>> = db.categoryDao().getAllCategories()

    fun getTasksForDate(date: String): Flow<List<DailyTask>> =
        db.taskDao().getTasksForDate(date)

    suspend fun getTasksForDateSync(date: String): List<DailyTask> =
        db.taskDao().getTasksForDateSync(date)

    fun getGoalsForWeek(weekId: String): Flow<List<WeeklyGoal>> =
        db.weeklyGoalDao().getGoalsForWeek(weekId)

    fun getActiveWeeklyGoals(): Flow<List<WeeklyGoal>> =
        db.weeklyGoalDao().getActiveWeeklyGoals()

    fun getTodayString(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    fun getTomorrowString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }

    fun getCurrentWeekId(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.YEAR)}-W${cal.get(Calendar.WEEK_OF_YEAR)}"
    }

    suspend fun getProfileSync(): UserProfile {
        return db.userProfileDao().getUserProfile() ?: ensureDefaultDataInitialized()
    }

    suspend fun ensureDefaultDataInitialized(): UserProfile {
        val today = getTodayString()
        val currentWeek = getCurrentWeekId()

        // 1. Profile check: if no UserProfile row, insert default profile (id 1, Emmanuel, alarm 7:00 enabled, evening 20:30, Sunday 21:00, all reminders enabled)
        var profile = db.userProfileDao().getUserProfile()
        if (profile == null) {
            val defaultProfile = UserProfile(
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
                lastActiveDate = today,
                streakShieldAvailable = true,
                lastShieldResetWeek = currentWeek,
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
                speechPitch = 0.85f,
                morningWinQuestion = "What is the one thing that would make today a win?",
                totalTasksCompleted = 0,
                perfectDaysCount = 0
            )
            db.userProfileDao().insertUserProfile(defaultProfile)
            profile = defaultProfile
        }

        // 2. Categories check: if categories table is empty, insert the 6 default categories
        val categories = db.categoryDao().getAllCategoriesSync()
        if (categories.isEmpty()) {
            val defaultCategories = listOf(
                CategoryItem(name = "Spiritual", colorHex = "#8B5CF6", iconEmoji = "✨", description = "devotion, prayer, sermons", isDefault = true),
                CategoryItem(name = "Law", colorHex = "#3B82F6", iconEmoji = "⚖️", description = "lectures, study, assignments", isDefault = true),
                CategoryItem(name = "Mentorship", colorHex = "#10B981", iconEmoji = "🌱", description = "daily report, book reading, voice notes", isDefault = true),
                CategoryItem(name = "Editing / Bezalim", colorHex = "#F59E0B", iconEmoji = "🎬", description = "clipping, video projects", isDefault = true),
                CategoryItem(name = "Fellowship", colorHex = "#EC4899", iconEmoji = "🤝", description = "tech unit, meetings", isDefault = true),
                CategoryItem(name = "Personal", colorHex = "#06B6D4", iconEmoji = "🌿", description = "health, rest, family", isDefault = true)
            )
            db.categoryDao().insertAllCategories(defaultCategories)
        }

        // 3. Badges check: if missing, insert initial badges
        val badge = db.badgeDao().getBadgeByCode("FIRST_VICTORY")
        if (badge == null) {
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
            db.badgeDao().insertAll(initialBadges)
        }

        // 4. Quotes check: if quotes table is empty, insert quotes
        if (db.quoteDao().getQuoteCount() == 0) {
            db.quoteDao().insertAll(com.example.data.quotes.QuoteBank.quotes)
        }

        // 5. Daily routine & weekly tasks check: recreate without duplicates
        val todayTasks = db.taskDao().getTasksForDateSync(today)
        val routineTemplates = listOf(
            DailyTask(
                title = "On waking: Devotion and prayer, write one line of what I learned",
                notes = "Spiritual foundation for the day",
                date = today,
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
                date = today,
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
                date = today,
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
                date = today,
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

        for (rt in routineTemplates) {
            if (todayTasks.none { it.title.equals(rt.title, ignoreCase = true) }) {
                db.taskDao().insertTask(rt)
            }
        }

        fun getDateForDayOfWeek(targetDay: Int): String {
            val c = Calendar.getInstance()
            c.set(Calendar.DAY_OF_WEEK, targetDay)
            return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.time)
        }

        val tuesdayDate = getDateForDayOfWeek(Calendar.TUESDAY)
        val fridayDate = getDateForDayOfWeek(Calendar.FRIDAY)
        val saturdayDate = getDateForDayOfWeek(Calendar.SATURDAY)

        val tTasks = db.taskDao().getTasksForDateSync(tuesdayDate)
        if (tTasks.none { it.title.contains("Sermon 1", ignoreCase = true) }) {
            db.taskDao().insertTask(
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
        }

        val fTasks = db.taskDao().getTasksForDateSync(fridayDate)
        if (fTasks.none { it.title.contains("Sermon 2", ignoreCase = true) }) {
            db.taskDao().insertTask(
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
        }

        val sTasks = db.taskDao().getTasksForDateSync(saturdayDate)
        if (sTasks.none { it.title.contains("voice note", ignoreCase = true) }) {
            db.taskDao().insertTask(
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
        }

        return checkAndRefreshStreakAndShield()
    }

    /**
     * Nightly maintenance:
     * 1. Rollover unfinished past tasks to today automatically with isMovedFromPreviousDay = true (no XP lost, no streak broken!)
     * 2. Ensure daily routine items exist for today
     * 3. Ensure day-specific weekly tasks exist for today (Tuesday, Friday, Saturday)
     * 4. Refresh streak and weekly streak shield
     */
    suspend fun checkAndRefreshStreakAndShield(): UserProfile {
        var profile = db.userProfileDao().getUserProfile() ?: UserProfile(userName = "Emmanuel")
        val today = getTodayString()
        val currentWeek = getCurrentWeekId()

        // 1. Rollover unfinished past tasks to today with "Moved" tag (Unfinished tasks are NOT failures!)
        val unfinishedPast = db.taskDao().getUnfinishedTasksBeforeDate(today)
        if (unfinishedPast.isNotEmpty()) {
            val movedTasks = unfinishedPast.map { task ->
                task.copy(date = today, isMovedFromPreviousDay = true)
            }
            db.taskDao().updateAllTasks(movedTasks)
        }

        // 2. Ensure daily routine tasks exist for today
        val todayTasks = db.taskDao().getTasksForDateSync(today)
        val hasDevotion = todayTasks.any { it.title.contains("Devotion", ignoreCase = true) }
        if (!hasDevotion) {
            val routineTasks = listOf(
                DailyTask(
                    title = "On waking: Devotion and prayer, write one line of what I learned",
                    notes = "Spiritual foundation for the day",
                    date = today,
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
                    date = today,
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
                    date = today,
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
                    date = today,
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
            db.taskDao().insertAllTasks(routineTasks)
        }

        // 3. Weekly tasks for specific days
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        when (dayOfWeek) {
            Calendar.TUESDAY -> {
                val hasSermon1 = todayTasks.any { it.title.contains("Sermon 1", ignoreCase = true) }
                if (!hasSermon1) {
                    db.taskDao().insertTask(
                        DailyTask(
                            title = "Tuesday: Listen to Sermon 1 and write what I learned",
                            notes = "Spiritual sermon reflection",
                            date = today,
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
                }
            }
            Calendar.FRIDAY -> {
                val hasSermon2 = todayTasks.any { it.title.contains("Sermon 2", ignoreCase = true) }
                if (!hasSermon2) {
                    db.taskDao().insertTask(
                        DailyTask(
                            title = "Friday: Listen to Sermon 2 and write what I learned",
                            notes = "Spiritual sermon reflection",
                            date = today,
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
                }
            }
            Calendar.SATURDAY -> {
                val hasVoiceNote = todayTasks.any { it.title.contains("voice note", ignoreCase = true) }
                if (!hasVoiceNote) {
                    db.taskDao().insertTask(
                        DailyTask(
                            title = "Saturday: Record my weekly book voice note before 9pm",
                            notes = "Mentorship weekly audio summary",
                            date = today,
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
                }
            }
        }

        var updated = profile

        // 4. Recharge streak shield weekly
        if (updated.lastShieldResetWeek != currentWeek) {
            updated = updated.copy(
                streakShieldAvailable = true,
                lastShieldResetWeek = currentWeek
            )
        }

        // 5. Streak continuity check
        if (updated.lastActiveDate.isNotEmpty() && updated.lastActiveDate != today) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val lastDate = sdf.parse(updated.lastActiveDate)
                val currDate = sdf.parse(today)
                if (lastDate != null && currDate != null) {
                    val diffDays = (currDate.time - lastDate.time) / (1000 * 60 * 60 * 24)
                    if (diffDays > 1) {
                        if (updated.streakShieldAvailable) {
                            updated = updated.copy(
                                streakShieldAvailable = false,
                                lastActiveDate = today
                            )
                        } else {
                            updated = updated.copy(
                                currentStreak = 1,
                                listCompletionStreak = 0,
                                lastActiveDate = today
                            )
                        }
                    }
                }
            } catch (e: Exception) {}
        }

        if (updated != profile) {
            db.userProfileDao().updateUserProfile(updated)
        }
        return updated
    }

    data class TaskCompletionResult(
        val xpGained: Int,
        val coinsGained: Int,
        val didLevelUp: Boolean,
        val newLevel: Int,
        val newLevelTitle: String,
        val unlockedBadges: List<BadgeItem>,
        val isDailyCleanSweep: Boolean,
        val isPerfectWeek: Boolean,
        val listCompletionStreak: Int
    )

    suspend fun toggleTaskCompletion(task: DailyTask): TaskCompletionResult? {
        val willBeCompleted = !task.isCompleted
        val updatedTask = task.copy(
            isCompleted = willBeCompleted,
            completedAt = if (willBeCompleted) System.currentTimeMillis() else null
        )
        db.taskDao().updateTask(updatedTask)

        if (!willBeCompleted) return null

        var profile = db.userProfileDao().getUserProfile() ?: UserProfile(userName = "Emmanuel")
        val today = getTodayString()
        val currentWeek = getCurrentWeekId()

        var earnedXp = task.xpReward
        var earnedCoins = task.coinReward
        val unlockedBadges = mutableListOf<BadgeItem>()

        var currentStreak = profile.currentStreak
        var highestStreak = profile.highestStreak
        if (profile.lastActiveDate != today) {
            currentStreak += 1
            if (currentStreak > highestStreak) highestStreak = currentStreak
        }

        // Check if all today's tasks are completed
        val todayTasks = db.taskDao().getTasksForDateSync(today)
        val visibleTasks = if (profile.busyDayMode) todayTasks.filter { it.isEssential || it.isRoutine } else todayTasks
        val isCleanSweep = visibleTasks.isNotEmpty() && visibleTasks.all { it.id == task.id || it.isCompleted }

        var listStreak = profile.listCompletionStreak
        var highestListStreak = profile.highestListCompletionStreak
        if (isCleanSweep) {
            earnedCoins += 50
            earnedXp += 80
            listStreak += 1
            if (listStreak > highestListStreak) highestListStreak = listStreak
            profile = profile.copy(perfectDaysCount = profile.perfectDaysCount + 1)
        }

        // Perfect week check
        val weekGoals = db.weeklyGoalDao().getGoalsForWeekSync(currentWeek)
        var isPerfectWeek = false
        if (weekGoals.isNotEmpty()) {
            val allMet = weekGoals.all { goal ->
                val linkedTasks = db.taskDao().getTasksForWeeklyGoalSync(goal.id)
                val completedCount = linkedTasks.count { it.isCompleted || it.id == task.id }
                completedCount >= goal.targetCount
            }
            if (allMet) {
                isPerfectWeek = true
                earnedCoins += 250
                earnedXp += 500
                profile = profile.copy(perfectWeeksCount = profile.perfectWeeksCount + 1)
            }
        }

        val newTotalXp = profile.xp + earnedXp
        val newTotalCoins = profile.coins + earnedCoins
        val newTotalTasks = profile.totalTasksCompleted + 1
        val (calcLevel, calcTitle) = calculateLevel(newTotalXp)
        val didLevelUp = calcLevel > profile.level

        // Check milestone badges
        val checkBadges = listOf(
            Triple("FIRST_VICTORY", true, 50),
            Triple("STREAK_3", currentStreak >= 3, 80),
            Triple("STREAK_7", currentStreak >= 7, 150),
            Triple("STREAK_30", currentStreak >= 30, 500),
            Triple("LIST_MASTER_3", listStreak >= 3, 100),
            Triple("LIST_MASTER_7", listStreak >= 7, 250),
            Triple("CLEAN_SWEEP", isCleanSweep, 100),
            Triple("PERFECT_WEEK", isPerfectWeek, 350)
        )

        for ((code, condition, xpBonus) in checkBadges) {
            if (condition) {
                val badge = db.badgeDao().getBadgeByCode(code)
                if (badge != null && !badge.isUnlocked) {
                    val unlocked = badge.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis())
                    db.badgeDao().updateBadge(unlocked)
                    unlockedBadges.add(unlocked)
                }
            }
        }

        val updatedProfile = profile.copy(
            xp = newTotalXp,
            coins = newTotalCoins,
            level = calcLevel,
            levelTitle = calcTitle,
            currentStreak = currentStreak,
            highestStreak = highestStreak,
            listCompletionStreak = listStreak,
            highestListCompletionStreak = highestListStreak,
            lastActiveDate = today,
            totalTasksCompleted = newTotalTasks
        )
        db.userProfileDao().updateUserProfile(updatedProfile)

        return TaskCompletionResult(
            xpGained = earnedXp,
            coinsGained = earnedCoins,
            didLevelUp = didLevelUp,
            newLevel = calcLevel,
            newLevelTitle = calcTitle,
            unlockedBadges = unlockedBadges,
            isDailyCleanSweep = isCleanSweep,
            isPerfectWeek = isPerfectWeek,
            listCompletionStreak = listStreak
        )
    }

    private fun calculateLevel(xp: Int): Pair<Int, String> {
        return when {
            xp >= 4500 -> Pair(8, "Mythic Champion")
            xp >= 3000 -> Pair(7, "Ascendant")
            xp >= 2000 -> Pair(6, "Grandmaster")
            xp >= 1200 -> Pair(5, "Legend")
            xp >= 700 -> Pair(4, "Master")
            xp >= 350 -> Pair(3, "Achiever")
            xp >= 150 -> Pair(2, "Builder")
            else -> Pair(1, "Starter")
        }
    }

    suspend fun getQuoteOfTheDay(): Quote {
        var quote = db.quoteDao().getNextUnusedQuote()
        if (quote == null) {
            db.quoteDao().resetAllQuotesUsed()
            quote = db.quoteDao().getNextUnusedQuote() ?: Quote(
                text = "Commit your work to the Lord, and your plans will be established.",
                author = "Proverbs 16:3",
                category = "Scripture",
                isBibleVerse = true
            )
        }
        db.quoteDao().markQuoteAsUsed(quote.id)
        return quote
    }

    suspend fun peekOrGetQuoteForBriefing(): Quote {
        return db.quoteDao().getNextUnusedQuote() ?: Quote(
            text = "Commit your work to the Lord, and your plans will be established.",
            author = "Proverbs 16:3",
            category = "Scripture",
            isBibleVerse = true
        )
    }

    // Weekly Chart Data structures
    data class DayProgress(
        val dayName: String, // "Mon", "Tue", etc.
        val dateString: String,
        val completedCount: Int,
        val categoryBreakdown: Map<String, Int> // CategoryName -> count
    )

    data class WeeklyChartReport(
        val days: List<DayProgress>,
        val thisWeekTotalCompleted: Int,
        val lastWeekTotalCompleted: Int,
        val percentChangeVsLastWeek: Int,
        val categoryDistribution: Map<String, Int> // CategoryName -> total count
    )

    suspend fun getWeeklyProgressReport(): WeeklyChartReport {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        // Calculate this week Monday to Sunday
        val cal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val days = mutableListOf<DayProgress>()
        val categoryTotals = mutableMapOf<String, Int>()

        for (i in 0 until 7) {
            val dStr = sdf.format(cal.time)
            val dName = dayFormat.format(cal.time)
            val tasks = db.taskDao().getTasksForDateSync(dStr).filter { it.isCompleted }
            val breakdown = tasks.groupingBy { it.categoryName }.eachCount()

            breakdown.forEach { (cat, count) ->
                categoryTotals[cat] = (categoryTotals[cat] ?: 0) + count
            }

            days.add(DayProgress(dayName = dName, dateString = dStr, completedCount = tasks.size, categoryBreakdown = breakdown))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }

        val thisWeekTotal = days.sumOf { it.completedCount }

        // Calculate last week
        val lastWeekCal = Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        val lastWeekStart = sdf.format(lastWeekCal.time)
        lastWeekCal.add(Calendar.DAY_OF_YEAR, 6)
        val lastWeekEnd = sdf.format(lastWeekCal.time)

        val lastWeekTasks = db.taskDao().getCompletedTasksBetweenDatesSync(lastWeekStart, lastWeekEnd)
        val lastWeekTotal = lastWeekTasks.size

        val diff = thisWeekTotal - lastWeekTotal
        val percentChange = if (lastWeekTotal > 0) {
            ((diff.toFloat() / lastWeekTotal) * 100).toInt()
        } else {
            if (thisWeekTotal > 0) 100 else 0
        }

        return WeeklyChartReport(
            days = days,
            thisWeekTotalCompleted = thisWeekTotal,
            lastWeekTotalCompleted = lastWeekTotal,
            percentChangeVsLastWeek = percentChange,
            categoryDistribution = categoryTotals
        )
    }

    suspend fun completeSundayCheckIn(): Pair<Int, Int> {
        val profile = db.userProfileDao().getUserProfile() ?: return Pair(0, 0)
        val bonusXp = 200
        val bonusCoins = 100
        val newXp = profile.xp + bonusXp
        val newCoins = profile.coins + bonusCoins
        val (calcLevel, calcTitle) = calculateLevel(newXp)

        val updated = profile.copy(
            xp = newXp,
            coins = newCoins,
            level = calcLevel,
            levelTitle = calcTitle,
            lastSundayCheckInWeek = getCurrentWeekId()
        )
        db.userProfileDao().updateUserProfile(updated)

        val badge = db.badgeDao().getBadgeByCode("SUNDAY_REVIEWER")
        if (badge != null && !badge.isUnlocked) {
            db.badgeDao().updateBadge(badge.copy(isUnlocked = true, unlockedAt = System.currentTimeMillis()))
        }
        return Pair(bonusXp, bonusCoins)
    }

    // CRUD
    suspend fun insertTask(task: DailyTask): Long = db.taskDao().insertTask(task)
    suspend fun updateTask(task: DailyTask) = db.taskDao().updateTask(task)
    suspend fun deleteTask(task: DailyTask) = db.taskDao().deleteTask(task)

    suspend fun insertWeeklyGoal(goal: WeeklyGoal): Long = db.weeklyGoalDao().insertWeeklyGoal(goal)
    suspend fun updateWeeklyGoal(goal: WeeklyGoal) = db.weeklyGoalDao().updateWeeklyGoal(goal)
    suspend fun deleteWeeklyGoal(goal: WeeklyGoal) = db.weeklyGoalDao().deleteWeeklyGoal(goal)

    suspend fun insertCategory(category: CategoryItem): Long = db.categoryDao().insertCategory(category)
    suspend fun updateCategory(category: CategoryItem) = db.categoryDao().updateCategory(category)
    suspend fun deleteCategory(category: CategoryItem) = db.categoryDao().deleteCategory(category)

    suspend fun redeemReward(reward: RewardItem): Boolean {
        val profile = db.userProfileDao().getUserProfile() ?: return false
        if (profile.coins < reward.coinPrice) return false

        val updatedProfile = profile.copy(coins = profile.coins - reward.coinPrice)
        db.userProfileDao().updateUserProfile(updatedProfile)

        val updatedReward = reward.copy(
            timesClaimed = reward.timesClaimed + 1,
            lastClaimedAt = System.currentTimeMillis()
        )
        db.rewardDao().updateReward(updatedReward)
        return true
    }

    suspend fun insertReward(reward: RewardItem): Long = db.rewardDao().insertReward(reward)
    suspend fun deleteReward(reward: RewardItem) = db.rewardDao().deleteReward(reward)
    suspend fun updateProfile(profile: UserProfile) = db.userProfileDao().insertUserProfile(profile.copy(id = 1))
}
