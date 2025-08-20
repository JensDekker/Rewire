package com.example.rewire.manager

import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitCompletion
import com.example.rewire.db.entity.HabitNoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HabitManagerTest {
    @Test
    fun testGetHabitsDueOn_empty() = runBlocking {
        habitList.clear()
        val due = manager.getHabitsDueOn("2025-08-19")
        assertTrue(due.isEmpty())
    }

    @Test
    fun testCreateHabit_duplicate() = runBlocking {
        habitList.clear()
    val habit = HabitEntity(1L, "Dup", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01")
    manager.createHabit(habit)
    manager.createHabit(habit)
    assertEquals(2, habitList.count { it.id == 1L })
    }

    @Test
    fun testCompleteHabit_invalid() = runBlocking {
        completionList.clear()
        manager.completeHabit(999, "2025-08-19")
        assertTrue(completionList.isEmpty())
    }

    @Test
    fun testInsertNote_duplicate() = runBlocking {
        noteList.clear()
    val note = HabitNoteEntity(1L, 1L, "DupNote", "2025-08-19T10:00")
    manager.insertNote(note)
    manager.insertNote(note)
    assertEquals(2, noteList.count { it.id == 1L })
    }
    // Simple in-memory lists to simulate DB
    private val habitList = mutableListOf<HabitEntity>()
    private val completionList = mutableListOf<HabitCompletion>()
    private val noteList = mutableListOf<HabitNoteEntity>()

    // Fake Repositories/DAOs matching Room signatures
    private val habitRepository = com.example.rewire.repository.HabitRepository(object : com.example.rewire.db.dao.HabitDao {
        override suspend fun getAll(): List<HabitEntity> = habitList
        override suspend fun getById(id: Long): HabitEntity? = habitList.find { it.id == id }
        override suspend fun insert(habit: HabitEntity): Long {
            habitList.add(habit)
            return habit.id
        }
        override suspend fun update(habit: HabitEntity) {
            habitList.replaceAll { if (it.id == habit.id) habit else it }
        }
        override suspend fun delete(habit: HabitEntity) {
            habitList.removeIf { it.id == habit.id }
        }
        override suspend fun deleteAll() {
            habitList.clear()
        }
    })

    private val habitCompletionRepository = com.example.rewire.repository.HabitCompletionRepository(object : com.example.rewire.db.dao.HabitCompletionDao {
        override suspend fun insertCompletion(completion: HabitCompletion) {
            // Only add completion if habitId exists in habitList
            if (habitList.any { it.id == completion.habitId }) {
                completionList.add(completion)
            }
        }
        override suspend fun deleteCompletion(habitId: Long, date: String) {
            completionList.removeIf { it.habitId == habitId && it.date == date }
        }
        override suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletion> =
            completionList.filter { it.habitId == habitId }
    })

    private val habitNoteRepository = com.example.rewire.repository.HabitNoteRepository(object : com.example.rewire.db.dao.HabitNoteDao {
        override suspend fun getAll(): List<HabitNoteEntity> = noteList
        override suspend fun getById(id: Long): HabitNoteEntity? = noteList.find { it.id == id }
        override suspend fun insert(note: HabitNoteEntity): Long {
            noteList.add(note)
            return note.id
        }
        override suspend fun update(note: HabitNoteEntity) {
            noteList.replaceAll { if (it.id == note.id) note else it }
        }
        override suspend fun delete(note: HabitNoteEntity) {
            noteList.removeIf { it.id == note.id }
        }
        override suspend fun deleteAll() {
            noteList.clear()
        }
    })

    private val manager = HabitManager(habitRepository, habitCompletionRepository, habitNoteRepository)

    @Test
    fun testGetHabitsDueOn() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(1L, "Test", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01"))
        val due = manager.getHabitsDueOn("2025-08-19")
        assertEquals(1, due.size)
        assertEquals("Test", due[0].name)
    }

    @Test
    fun testCreateHabit() = runBlocking {
        habitList.clear()
    manager.createHabit(HabitEntity(2L, "New", com.example.rewire.core.RecurrenceType.Daily, "09:00", 15, "2025-08-10"))
    assertTrue(habitList.any { it.name == "New" })
    }

    @Test
    fun testCompleteHabit() = runBlocking {
        completionList.clear()
        habitList.clear()
        habitList.add(HabitEntity(1L, "Test", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01"))
        manager.completeHabit(1L, "2025-08-19")
        assertTrue(completionList.any { it.habitId == 1L && it.date == "2025-08-19" })
    }

    @Test
    fun testDeleteCompletion() = runBlocking {
        completionList.clear()
    completionList.add(HabitCompletion(1L, 1L, "2025-08-19"))
    manager.deleteCompletion(1L, "2025-08-19")
    assertFalse(completionList.any { it.habitId == 1L && it.date == "2025-08-19" })
    }

    @Test
    fun testGetCompletionsForHabit() = runBlocking {
        completionList.clear()
    completionList.add(HabitCompletion(1L, 1L, "2025-08-19"))
    val completions = manager.getCompletionsForHabit(1L)
    assertEquals(1, completions.size)
    assertEquals("2025-08-19", completions[0].date)
    }

    @Test
    fun testInsertNote() = runBlocking {
        noteList.clear()
    manager.insertNote(HabitNoteEntity(1L, 1L, "Note", "2025-08-19T10:00"))
    assertTrue(noteList.any { it.content == "Note" })
    }

    @Test
    fun testEditNote() = runBlocking {
        noteList.clear()
    noteList.add(HabitNoteEntity(2L, 1L, "Old", "2025-08-19T10:00"))
    manager.editNote(HabitNoteEntity(2L, 1L, "Updated", "2025-08-19T11:00"))
    assertEquals("Updated", noteList.find { it.id == 2L }?.content)
    }

    @Test
    fun testDeleteNote() = runBlocking {
        noteList.clear()
    noteList.add(HabitNoteEntity(3L, 1L, "DeleteMe", "2025-08-19T10:00"))
    manager.deleteNote(HabitNoteEntity(3L, 1L, "DeleteMe", "2025-08-19T10:00"))
    assertFalse(noteList.any { it.id == 3L })
    }

    @Test
    fun testGetNotesForHabit() = runBlocking {
        noteList.clear()
    noteList.add(HabitNoteEntity(4L, 2L, "Note1", "2025-08-19T10:00"))
    noteList.add(HabitNoteEntity(5L, 2L, "Note2", "2025-08-19T11:00"))
    val notes = manager.getNotesForHabit(2L)
        assertEquals(2, notes.size)
    }
}
