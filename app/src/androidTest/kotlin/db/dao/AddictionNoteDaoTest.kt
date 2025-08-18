package com.example.rewire.db.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AddictionNoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AddictionNoteDaoTest {
    private lateinit var db: com.example.rewire.db.RewireDatabase
    private lateinit var addictionHabitDao: com.example.rewire.db.dao.AddictionHabitDao
    private lateinit var addictionNoteDao: com.example.rewire.db.dao.AddictionNoteDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, com.example.rewire.db.RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        addictionHabitDao = db.addictionHabitDao()
        addictionNoteDao = db.addictionNoteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun addictionNoteDao_relationship_with_addictionHabit() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 20L,
                name = "Addiction with Notes",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            val note1 = AddictionNoteEntity(id = 201L, addictionId = 20L, content = "A Note 1", timestamp = "2025-08-08T14:00:00Z")
            addictionNoteDao.insert(note1)
            val notes = addictionNoteDao.getAll().filter { it.addictionId == 20L }
            assertEquals(1, notes.size)
            assertEquals("A Note 1", notes[0].content)
        }
    }

    @Test(expected = Exception::class)
    fun insertNoteForNonExistentAddiction_shouldThrow() {
        runBlocking {
            val note = AddictionNoteEntity(id = 5001L, addictionId = 9999L, content = "Orphan", timestamp = "2025-08-08T12:00:00Z")
            addictionNoteDao.insert(note)
        }
    }

    @Test
    fun cascadeDeleteNotes_whenAddictionDeleted() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 100L,
                name = "Cascade Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            val note1 = AddictionNoteEntity(id = 1001L, addictionId = 100L, content = "Cascade Note 1", timestamp = "2025-08-08T12:00:00Z")
            addictionNoteDao.insert(note1)
            addictionHabitDao.delete(addiction)
            val notes = addictionNoteDao.getAll().filter { it.addictionId == 100L }
            assertTrue(notes.isEmpty())
        }
    }
}
