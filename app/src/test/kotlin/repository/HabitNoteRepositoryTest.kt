package com.example.rewire.repository

import com.example.rewire.db.entity.HabitNoteEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HabitNoteRepositoryTest {
    @Test
    fun testGetNotesForHabit_empty() = runBlocking {
        noteList.clear()
        val notes = repo.getNotesForHabit(999)
        assertTrue(notes.isEmpty())
    }

    @Test
    fun testInsertNote_duplicate() = runBlocking {
        noteList.clear()
    val note = HabitNoteEntity(1L, 1L, "DupNote", "2025-08-19T10:00")
    repo.insertNote(note)
    repo.insertNote(note)
    assertEquals(2, noteList.count { it.id == 1L })
    }

    @Test
    fun testDeleteNote_invalid() = runBlocking {
        noteList.clear()
    val note = HabitNoteEntity(999L, 1L, "NotFound", "2025-08-19T10:00")
    repo.deleteNote(note)
    assertTrue(noteList.isEmpty())
    }
    // In-memory list to simulate DB
    private val noteList = mutableListOf<HabitNoteEntity>()

    // Fake DAO matching Room signatures
    private val dao = object : com.example.rewire.db.dao.HabitNoteDao {
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
    }

    private val repo = com.example.rewire.repository.HabitNoteRepository(dao)

    @Test
    fun testEditNote() = runBlocking {
        noteList.clear()
    noteList.add(HabitNoteEntity(1L, 1L, "Old", "2025-08-19T10:00"))
    repo.editNote(HabitNoteEntity(1L, 1L, "Updated", "2025-08-19T11:00"))
    assertEquals("Updated", noteList.find { it.id == 1L }?.content)
    }

    @Test
    fun testInsertNote() = runBlocking {
        noteList.clear()
    val note = HabitNoteEntity(2L, 1L, "InsertMe", "2025-08-19T12:00")
    repo.insertNote(note)
    assertTrue(noteList.any { it.id == 2L && it.habitId == 1L && it.content == "InsertMe" })
    }

    @Test
    fun testDeleteNote() = runBlocking {
        noteList.clear()
    val note = HabitNoteEntity(3L, 1L, "DeleteMe", "2025-08-19T13:00")
    noteList.add(note)
    repo.deleteNote(note)
    assertFalse(noteList.any { it.id == 3L && it.habitId == 1L && it.content == "DeleteMe" })
    }

    @Test
    fun testGetNotesForHabit() = runBlocking {
        noteList.clear()
        val n1 = HabitNoteEntity(4, 2, "Note1", "2025-08-19T14:00")
        val n2 = HabitNoteEntity(5, 2, "Note2", "2025-08-19T15:00")
        noteList.addAll(listOf(n1, n2))
        val notes = repo.getNotesForHabit(2)
        assertEquals(2, notes.size)
        assertTrue(notes.contains(n1))
        assertTrue(notes.contains(n2))
    }
}
