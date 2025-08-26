package repository

import com.example.rewire.db.entity.AddictionNoteEntity
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.repository.AddictionNoteRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeAddictionNoteDao : com.example.rewire.db.dao.AddictionNoteDao {
    private val notes = mutableListOf<AddictionNoteEntity>()
    private var idCounter = 1L
    override suspend fun getAll(): List<AddictionNoteEntity> = notes.toList()
    override suspend fun getById(id: Long): AddictionNoteEntity? = notes.find { it.id == id }
    override suspend fun insert(note: AddictionNoteEntity): Long {
        val newNote = note.copy(id = idCounter++)
        notes.add(newNote)
        return newNote.id
    }
    override suspend fun update(note: AddictionNoteEntity) {
        val idx = notes.indexOfFirst { it.id == note.id }
        if (idx != -1) notes[idx] = note
    }
    override suspend fun delete(note: AddictionNoteEntity) {
        notes.removeIf { it.id == note.id }
    }
    override suspend fun deleteAll() {
        notes.clear()
    }
}

class AddictionNoteRepositoryTest {
    private lateinit var repo: AddictionNoteRepository
    private lateinit var dao: FakeAddictionNoteDao

    @Before
    fun setup() {
        dao = FakeAddictionNoteDao()
        repo = AddictionNoteRepository(dao)
    }

    @Test
    fun testInsertAndGetById() = runBlocking {
        val note = AddictionNoteEntity(id = 0, addictionId = 1L, content = "First note", timestamp = "2025-08-20T09:00:00")
        val id = repo.insert(note)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals("First note", fetched?.content)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AddictionNoteEntity(id = 0, addictionId = 1L, content = "Note 1", timestamp = "2025-08-20T09:00:00"))
        repo.insert(AddictionNoteEntity(id = 0, addictionId = 1L, content = "Note 2", timestamp = "2025-08-21T09:00:00"))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AddictionNoteEntity(id = 0, addictionId = 1L, content = "Old content", timestamp = "2025-08-22T09:00:00"))
        val updated = AddictionNoteEntity(id = id, addictionId = 1L, content = "New content", timestamp = "2025-08-22T09:00:00")
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals("New content", fetched?.content)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AddictionNoteEntity(0, 1L, "Note to delete", "Delete me"))
        val note = repo.getById(id)!!
        repo.delete(note)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AddictionNoteEntity(id = 0, addictionId = 1L, content = "Note A", timestamp = "2025-08-23T09:00:00"))
        repo.insert(AddictionNoteEntity(id = 0, addictionId = 1L, content = "Note B", timestamp = "2025-08-24T09:00:00"))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
