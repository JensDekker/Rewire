package repository

import com.example.rewire.db.entity.AddictionNoteEntity
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
        val note = AddictionNoteEntity(0, "First note", "This is a note.")
        val id = repo.insert(note)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals("First note", fetched?.title)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AddictionNoteEntity(0, "Note 1", "Content 1"))
        repo.insert(AddictionNoteEntity(0, "Note 2", "Content 2"))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AddictionNoteEntity(0, "Note", "Old content"))
        val updated = AddictionNoteEntity(id, "Note", "New content")
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals("New content", fetched?.content)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AddictionNoteEntity(0, "Note to delete", "Delete me"))
        val note = repo.getById(id)!!
        repo.delete(note)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AddictionNoteEntity(0, "Note A", "A"))
        repo.insert(AddictionNoteEntity(0, "Note B", "B"))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
