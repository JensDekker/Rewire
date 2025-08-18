package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitNoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class HabitNoteDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var habitDao: com.example.rewire.db.dao.HabitDao
    private lateinit var habitNoteDao: com.example.rewire.db.dao.HabitNoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
        habitNoteDao = db.habitNoteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitNoteDao_crudOperations() {
        runBlocking {
            val habit = HabitEntity(
                id = 1L,
                name = "Test Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val note = HabitNoteEntity(id = 1L, habitId = 1L, content = "Test note", timestamp = "2025-08-08T12:00:00Z")
            val id = habitNoteDao.insert(note)
            val loaded = habitNoteDao.getById(id)
            assertNotNull(loaded)
            assertEquals("Test note", loaded?.content)
            val updated = loaded!!.copy(content = "Updated note")
            habitNoteDao.update(updated)
            val loaded2 = habitNoteDao.getById(id)
            assertEquals("Updated note", loaded2?.content)
            habitNoteDao.delete(updated)
            assertNull(habitNoteDao.getById(id))
        }
    }

    @Test(expected = Exception::class)
    fun insertNoteForNonExistentHabit_shouldThrow() {
        runBlocking {
            val note = HabitNoteEntity(id = 5001L, habitId = 9999L, content = "Orphan", timestamp = "2025-08-08T12:00:00Z")
            habitNoteDao.insert(note)
        }
    }

    @Test
    fun cascadeDeleteNotes_whenHabitDeleted() {
        runBlocking {
            val habit = HabitEntity(
                id = 100L,
                name = "Cascade Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val note1 = HabitNoteEntity(id = 1001L, habitId = 100L, content = "Cascade Note 1", timestamp = "2025-08-08T12:00:00Z")
            habitNoteDao.insert(note1)
            habitDao.delete(habit)
            val notes = habitNoteDao.getAll().filter { it.habitId == 100L }
            assertTrue(notes.isEmpty())
        }
    }
}
