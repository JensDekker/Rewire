package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitCompletion
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitCompletionDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var habitDao: com.example.rewire.db.dao.HabitDao
    private lateinit var habitCompletionDao: com.example.rewire.db.dao.HabitCompletionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
        habitCompletionDao = db.habitCompletionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitCompletionDao_crudOperations() = runBlocking {
        val habit = HabitEntity(
            id = 10000L,
            name = "Completion Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion = HabitCompletion(
            habitId = 10000L,
            date = "2025-08-18"
        )
        habitCompletionDao.insertCompletion(completion)
        val completions = habitCompletionDao.getCompletionsForHabit(10000L)
        assertEquals(1, completions.size)
        assertEquals("2025-08-18", completions[0].date)
        habitCompletionDao.deleteCompletion(10000L, "2025-08-18")
        val completionsAfterDelete = habitCompletionDao.getCompletionsForHabit(10000L)
        assertTrue(completionsAfterDelete.isEmpty())
    }
    @Test
    fun habitCompletionDao_queryByDate() = runBlocking {
        val habit = HabitEntity(
            id = 20000L,
            name = "Query Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion1 = HabitCompletion(habitId = 20000L, date = "2025-08-17")
        val completion2 = HabitCompletion(habitId = 20000L, date = "2025-08-18")
        habitCompletionDao.insertCompletion(completion1)
        habitCompletionDao.insertCompletion(completion2)
        val completions = habitCompletionDao.getCompletionsForHabit(20000L)
        assertEquals(2, completions.size)
        val dates = completions.map { it.date }
        assertTrue(dates.contains("2025-08-17"))
        assertTrue(dates.contains("2025-08-18"))
    }

    @Test
    fun queryCompletionsByDateRange() = runBlocking {
        val habit = HabitEntity(
            id = 30000L,
            name = "Range Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completions = listOf(
            HabitCompletion(habitId = 30000L, date = "2025-08-15"),
            HabitCompletion(habitId = 30000L, date = "2025-08-16"),
            HabitCompletion(habitId = 30000L, date = "2025-08-17")
        )
        completions.forEach { habitCompletionDao.insertCompletion(it) }
        val allCompletions = habitCompletionDao.getCompletionsForHabit(30000L)
        val filtered = allCompletions.filter { it.date >= "2025-08-16" && it.date <= "2025-08-17" }
        assertEquals(2, filtered.size)
    }

    @Test
    fun countCompletionsPerHabit() = runBlocking {
        val habit = HabitEntity(
            id = 40000L,
            name = "Count Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completions = listOf(
            HabitCompletion(habitId = 40000L, date = "2025-08-18"),
            HabitCompletion(habitId = 40000L, date = "2025-08-19")
        )
        completions.forEach { habitCompletionDao.insertCompletion(it) }
        val count = habitCompletionDao.getCompletionsForHabit(40000L).size
        assertEquals(2, count)
    }
}
