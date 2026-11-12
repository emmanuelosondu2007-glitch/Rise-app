package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BadgeItem
import com.example.data.model.CategoryItem
import com.example.data.model.DailyTask
import com.example.data.model.Quote
import com.example.data.model.RewardItem
import com.example.data.model.UserProfile
import com.example.data.model.WeeklyGoal
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY id ASC")
    fun getAllCategories(): Flow<List<CategoryItem>>

    @Query("SELECT * FROM categories ORDER BY id ASC")
    suspend fun getAllCategoriesSync(): List<CategoryItem>

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    suspend fun getCategoryById(id: Long): CategoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllCategories(categories: List<CategoryItem>)

    @Update
    suspend fun updateCategory(category: CategoryItem)

    @Delete
    suspend fun deleteCategory(category: CategoryItem)
}

@Dao
interface TaskDao {
    @Query("SELECT * FROM daily_tasks WHERE date = :date ORDER BY isCompleted ASC, isRoutine DESC, priority DESC, id ASC")
    fun getTasksForDate(date: String): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks WHERE date = :date ORDER BY isCompleted ASC, isRoutine DESC, priority DESC, id ASC")
    suspend fun getTasksForDateSync(date: String): List<DailyTask>

    @Query("SELECT * FROM daily_tasks WHERE date < :today AND isCompleted = 0")
    suspend fun getUnfinishedTasksBeforeDate(today: String): List<DailyTask>

    @Query("SELECT * FROM daily_tasks WHERE date >= :startDate AND date <= :endDate AND isCompleted = 1")
    fun getCompletedTasksBetweenDates(startDate: String, endDate: String): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks WHERE date >= :startDate AND date <= :endDate AND isCompleted = 1")
    suspend fun getCompletedTasksBetweenDatesSync(startDate: String, endDate: String): List<DailyTask>

    @Query("SELECT * FROM daily_tasks ORDER BY date DESC, id DESC")
    fun getAllTasks(): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks WHERE weeklyGoalId = :goalId")
    fun getTasksForWeeklyGoal(goalId: Long): Flow<List<DailyTask>>

    @Query("SELECT * FROM daily_tasks WHERE weeklyGoalId = :goalId")
    suspend fun getTasksForWeeklyGoalSync(goalId: Long): List<DailyTask>

    @Query("SELECT * FROM daily_tasks WHERE id = :id LIMIT 1")
    suspend fun getTaskById(id: Long): DailyTask?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: DailyTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<DailyTask>)

    @Update
    suspend fun updateTask(task: DailyTask)

    @Update
    suspend fun updateAllTasks(tasks: List<DailyTask>)

    @Delete
    suspend fun deleteTask(task: DailyTask)

    @Query("DELETE FROM daily_tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("SELECT COUNT(*) FROM daily_tasks WHERE isCompleted = 1")
    fun getTotalCompletedTasksCount(): Flow<Int>
}

@Dao
interface WeeklyGoalDao {
    @Query("SELECT * FROM weekly_goals WHERE isArchived = 0 ORDER BY id DESC")
    fun getActiveWeeklyGoals(): Flow<List<WeeklyGoal>>

    @Query("SELECT * FROM weekly_goals WHERE weekIdentifier = :weekId AND isArchived = 0 ORDER BY id ASC")
    fun getGoalsForWeek(weekId: String): Flow<List<WeeklyGoal>>

    @Query("SELECT * FROM weekly_goals WHERE weekIdentifier = :weekId AND isArchived = 0 ORDER BY id ASC")
    suspend fun getGoalsForWeekSync(weekId: String): List<WeeklyGoal>

    @Query("SELECT * FROM weekly_goals WHERE id = :id LIMIT 1")
    suspend fun getGoalById(id: Long): WeeklyGoal?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeeklyGoal(goal: WeeklyGoal): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllGoals(goals: List<WeeklyGoal>)

    @Update
    suspend fun updateWeeklyGoal(goal: WeeklyGoal)

    @Delete
    suspend fun deleteWeeklyGoal(goal: WeeklyGoal)

    @Query("DELETE FROM weekly_goals WHERE id = :id")
    suspend fun deleteGoalById(id: Long)
}

@Dao
interface RewardDao {
    @Query("SELECT * FROM rewards ORDER BY coinPrice ASC")
    fun getAllRewards(): Flow<List<RewardItem>>

    @Query("SELECT * FROM rewards WHERE id = :id LIMIT 1")
    suspend fun getRewardById(id: Long): RewardItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReward(reward: RewardItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rewards: List<RewardItem>)

    @Update
    suspend fun updateReward(reward: RewardItem)

    @Delete
    suspend fun deleteReward(reward: RewardItem)
}

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserProfile(profile: UserProfile)
}

@Dao
interface BadgeDao {
    @Query("SELECT * FROM badges ORDER BY id ASC")
    fun getAllBadges(): Flow<List<BadgeItem>>

    @Query("SELECT * FROM badges WHERE isUnlocked = 1 ORDER BY unlockedAt DESC")
    fun getUnlockedBadges(): Flow<List<BadgeItem>>

    @Query("SELECT * FROM badges WHERE code = :code LIMIT 1")
    suspend fun getBadgeByCode(code: String): BadgeItem?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(badges: List<BadgeItem>)

    @Update
    suspend fun updateBadge(badge: BadgeItem)
}

@Dao
interface QuoteDao {
    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getQuoteCount(): Int

    @Query("SELECT * FROM quotes WHERE wasUsed = 0 ORDER BY orderIndex ASC LIMIT 1")
    suspend fun getNextUnusedQuote(): Quote?

    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): Quote?

    @Query("SELECT * FROM quotes WHERE id = :id LIMIT 1")
    suspend fun getQuoteById(id: Long): Quote?

    @Query("UPDATE quotes SET wasUsed = 1 WHERE id = :id")
    suspend fun markQuoteAsUsed(id: Long)

    @Query("UPDATE quotes SET wasUsed = 0")
    suspend fun resetAllQuotesUsed()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(quotes: List<Quote>)
}
