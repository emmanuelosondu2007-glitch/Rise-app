package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.RiseDatabase
import com.example.data.model.DailyTask
import com.example.data.quotes.QuoteBank
import com.example.data.repository.RiseRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var db: RiseDatabase
    private lateinit var repository: RiseRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RiseDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = RiseRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Rise", appName)
    }

    @Test
    fun `verify quote bank has at least 365 quotes`() {
        val quotes = QuoteBank.quotes
        assertTrue("Quotes count must be >= 365, was ${quotes.size}", quotes.size >= 365)
    }

    @Test
    fun `deduplicateTasks deletes duplicates and keeps completed one`() = runBlocking {
        val today = repository.getTodayString()
        db.taskDao().insertAllTasks(
            listOf(
                DailyTask(title = "Read Law Notes", date = today, isCompleted = false),
                DailyTask(title = "Read Law Notes", date = today, isCompleted = true, completedAt = 12345L),
                DailyTask(title = "Read Law Notes", date = today, isCompleted = false)
            )
        )

        repository.deduplicateTasks()

        val remaining = db.taskDao().getTasksForDateSync(today)
        assertEquals(1, remaining.size)
        assertTrue(remaining.first().isCompleted)
    }

    @Test
    fun `midnight rollover ignores routine tasks and skips duplicate titles today`() = runBlocking {
        val today = repository.getTodayString()
        val yesterday = repository.getOffsetDateString(-1)

        // Seed today with one existing personal task and one routine task
        db.taskDao().insertTask(
            DailyTask(title = "Finish Brief", date = today, isCompleted = false, isRoutine = false)
        )
        // Seed yesterday with:
        // 1) An unfinished routine task -> must NOT roll over
        // 2) An unfinished personal task whose title already exists today -> must NOT duplicate today
        // 3) An unfinished unique personal task -> MUST roll over to today
        db.taskDao().insertAllTasks(
            listOf(
                DailyTask(
                    title = "Old Devotion Routine",
                    date = yesterday,
                    isCompleted = false,
                    isRoutine = true
                ),
                DailyTask(
                    title = "Finish Brief",
                    date = yesterday,
                    isCompleted = false,
                    isRoutine = false
                ),
                DailyTask(
                    title = "Call Mentor",
                    date = yesterday,
                    isCompleted = false,
                    isRoutine = false
                )
            )
        )

        repository.ensureDefaultDataInitialized()

        val todayTasks = db.taskDao().getTasksForDateSync(today)
        val yesterdayTasks = db.taskDao().getTasksForDateSync(yesterday)

        // Old routine stays on yesterday, never moved to today
        assertFalse(todayTasks.any { it.title == "Old Devotion Routine" })
        assertTrue(yesterdayTasks.any { it.title == "Old Devotion Routine" })

        // "Finish Brief" exists only once on today
        assertEquals(1, todayTasks.count { it.title == "Finish Brief" })

        // Unique personal task "Call Mentor" moved cleanly to today
        val movedTask = todayTasks.find { it.title == "Call Mentor" }
        assertTrue(movedTask != null && movedTask.isMovedFromPreviousDay)
    }

    @Test
    fun `ensureDefaultDataInitialized does not pre-create weekly tasks for other dates`() = runBlocking {
        repository.ensureDefaultDataInitialized()
        val today = repository.getTodayString()
        val allTasks = db.taskDao().getAllTasksSync()

        // No tasks created for future dates in the week
        assertTrue(allTasks.all { it.date == today })

        // Weekly task only exists today if today is Tue/Fri/Sat
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val weeklyTasks = allTasks.filter { it.scheduledDayOfWeek != null }
        when (dayOfWeek) {
            Calendar.TUESDAY, Calendar.FRIDAY, Calendar.SATURDAY -> {
                assertEquals(1, weeklyTasks.size)
                assertEquals(dayOfWeek, weeklyTasks.first().scheduledDayOfWeek)
            }
            else -> assertEquals(0, weeklyTasks.size)
        }
    }
}
