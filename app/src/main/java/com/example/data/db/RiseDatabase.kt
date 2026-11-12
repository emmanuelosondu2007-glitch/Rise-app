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
    version = 3,
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
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
