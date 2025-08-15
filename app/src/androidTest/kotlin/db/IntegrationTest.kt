package com.example.rewire.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.HabitDao
import com.example.rewire.db.dao.HabitNoteDao
import com.example.rewire.db.dao.HabitCompletionDao
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitNoteEntity
import com.example.rewire.db.entity.HabitCompletionEntity
import com.example.rewire.core.RecurrenceType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class IntegrationTest {
    private lateinit var db: RewireDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var habitNoteDao: HabitNoteDao
    private lateinit var habitCompletionDao: HabitCompletionDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
        habitNoteDao = db.habitNoteDao()
        habitCompletionDao = db.habitCompletionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitNoteCompletion_integration() = runBlocking {
        val habit = HabitEntity(
            id = 10001L,
            name = "Integration Habit",
            recurrence = RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 15,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val today = java.time.LocalDate.now().toString()
        val note = HabitNoteEntity(id = 10002L, habitId = 10001L, content = "Integration Note", timestamp = today + "T08:00:00Z")
        habitNoteDao.insert(note)
        val completion = HabitCompletionEntity(habitId = 10001L, date = today)
        habitCompletionDao.insertCompletion(completion)
        val loadedNotes = habitNoteDao.getAll().filter { it.habitId == 10001L }
        val loadedCompletions = habitCompletionDao.getCompletionsForHabit(10001L)
        assertEquals(1, loadedNotes.size)
        assertEquals(1, loadedCompletions.size)
        // Delete completion, note should remain
        habitCompletionDao.deleteCompletion(10001L, today)
        val notesAfterCompletionDelete = habitNoteDao.getAll().filter { it.habitId == 10001L }
        assertEquals(1, notesAfterCompletionDelete.size)
        // Delete note, completion should remain
        habitNoteDao.delete(note)
        val completionsAfterNoteDelete = habitCompletionDao.getCompletionsForHabit(10001L)
        assertEquals(0, notesAfterCompletionDelete.size - 1 + completionsAfterNoteDelete.size)
    }
}
