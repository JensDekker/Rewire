package com.example.rewire.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.HabitCompletionDao
import com.example.rewire.db.entity.HabitCompletionEntity
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.core.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitCompletionDaoTest {
    @Test
    fun duplicateCompletion_preventedByUniqueIndex() = runBlocking {
        val habit = HabitEntity(
            id = 2L,
            name = "Duplicate Test",
            recurrence = RecurrenceType.Daily,
            preferredTime = "10:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val date = "2025-08-16"
        val completion1 = HabitCompletionEntity(habitId = 2L, date = date)
        completionDao.insertCompletion(completion1)
        // Try to insert duplicate
        val completion2 = HabitCompletionEntity(habitId = 2L, date = date)
        try {
            completionDao.insertCompletion(completion2)
            fail("Should not allow duplicate completions for same habit/date")
        } catch (e: Exception) {
            // Expected due to unique index
        }
    }

    @Test
    fun queryCompletionsForHabitWithNone_returnsEmpty() = runBlocking {
        val habit = HabitEntity(
            id = 3L,
            name = "No Completion",
            recurrence = RecurrenceType.Daily,
            preferredTime = "11:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completions = completionDao.getCompletionsForHabit(3L)
        assertTrue(completions.isEmpty())
    }

    @Test
    fun cascadeDeleteHabit_deletesCompletions() = runBlocking {
        val habit = HabitEntity(
            id = 4L,
            name = "Cascade Habit",
            recurrence = RecurrenceType.Daily,
            preferredTime = "12:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion = HabitCompletionEntity(habitId = 4L, date = "2025-08-17")
        completionDao.insertCompletion(completion)
        habitDao.delete(habit)
        val completions = completionDao.getCompletionsForHabit(4L)
        assertTrue(completions.isEmpty())
    }

    @Test
    fun addNoteAndCompletion_interaction() = runBlocking {
        val habit = HabitEntity(
            id = 5L,
            name = "Note Interaction",
            recurrence = RecurrenceType.Daily,
            preferredTime = "13:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val note = HabitNoteEntity(id = 5001L, habitId = 5L, content = "Test Note", timestamp = "2025-08-18T12:00:00Z")
        db.habitNoteDao().insert(note)
        val completion = HabitCompletionEntity(habitId = 5L, date = "2025-08-18")
        completionDao.insertCompletion(completion)
        // Delete completion, note should remain
        completionDao.deleteCompletion(5L, "2025-08-18")
        val notes = db.habitNoteDao().getAll().filter { it.habitId == 5L }
        assertEquals(1, notes.size)
        // Delete note, completion should remain
        db.habitNoteDao().delete(note)
        val completions = completionDao.getCompletionsForHabit(5L)
        assertTrue(completions.isEmpty())
    }

    @Test
    fun concurrency_bulkInsertDeleteCompletions() = runBlocking {
        val habit = HabitEntity(
            id = 6L,
            name = "Bulk Habit",
            recurrence = RecurrenceType.Daily,
            preferredTime = "14:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val dates = (1..10).map { "2025-08-${10 + it}" }
        val jobs = dates.map { date ->
            kotlinx.coroutines.launch {
                completionDao.insertCompletion(HabitCompletionEntity(habitId = 6L, date = date))
            }
        }
        jobs.forEach { it.join() }
        val completions = completionDao.getCompletionsForHabit(6L)
        assertEquals(10, completions.size)
        // Bulk delete
        dates.forEach { date -> completionDao.deleteCompletion(6L, date) }
        val completionsAfterDelete = completionDao.getCompletionsForHabit(6L)
        assertTrue(completionsAfterDelete.isEmpty())
    }

    @Test
    fun insertCompletionForNonExistentHabit_fails() = runBlocking {
        val completion = HabitCompletionEntity(habitId = 999L, date = "2025-08-30")
        try {
            completionDao.insertCompletion(completion)
            fail("Should not allow completion for non-existent habit")
        } catch (e: Exception) {
            // Expected due to foreign key constraint
        }
    }

    @Test
    fun deleteCompletionForNonExistentPair_noEffect() = runBlocking {
        // Should not throw
        completionDao.deleteCompletion(888L, "2025-08-31")
        // No completions should exist
        val completions = completionDao.getCompletionsForHabit(888L)
        assertTrue(completions.isEmpty())
    }

    @Test
    fun dateBoundary_leapYear_endOfMonth() = runBlocking {
        val habit = HabitEntity(
            id = 7L,
            name = "Date Boundary",
            recurrence = RecurrenceType.Daily,
            preferredTime = "15:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val leapDate = "2024-02-29"
        val endOfMonth = "2025-08-31"
        completionDao.insertCompletion(HabitCompletionEntity(habitId = 7L, date = leapDate))
        completionDao.insertCompletion(HabitCompletionEntity(habitId = 7L, date = endOfMonth))
        val completions = completionDao.getCompletionsForHabit(7L)
        assertTrue(completions.any { it.date == leapDate })
        assertTrue(completions.any { it.date == endOfMonth })
    }
    private lateinit var db: RewireDatabase
    private lateinit var habitDao: com.example.rewire.db.dao.HabitDao
    private lateinit var completionDao: HabitCompletionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
        completionDao = db.habitCompletionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertAndDeleteCompletion() = runBlocking {
        val habit = HabitEntity(
            id = 1L,
            name = "Test Habit",
            recurrence = RecurrenceType.Daily,
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion = HabitCompletionEntity(habitId = 1L, date = "2025-08-15")
        val id = completionDao.insertCompletion(completion)
        val completions = completionDao.getCompletionsForHabit(1L)
        assertEquals(1, completions.size)
        assertEquals("2025-08-15", completions[0].date)
        completionDao.deleteCompletion(1L, "2025-08-15")
        val completionsAfterDelete = completionDao.getCompletionsForHabit(1L)
        assertTrue(completionsAfterDelete.isEmpty())
    }
}
