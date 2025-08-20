import com.example.rewire.db.entity.HabitNoteEntity
import com.example.rewire.db.dao.HabitNoteDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HabitNoteDaoTest {
	private val noteList = mutableListOf<HabitNoteEntity>()
	private val dao = object : HabitNoteDao {
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

	@Test
	fun testInsertAndGetAll() = runBlocking {
		noteList.clear()
		val note = HabitNoteEntity(1, 1, "TestNote", "2025-08-19T10:00")
		dao.insert(note)
		val all = dao.getAll()
		assertEquals(1, all.size)
		assertEquals("TestNote", all[0].content)
	}

	@Test
	fun testGetById() = runBlocking {
		noteList.clear()
		val note = HabitNoteEntity(2, 1, "FindMeNote", "2025-08-19T11:00")
		dao.insert(note)
		val found = dao.getById(2)
		assertNotNull(found)
		assertEquals("FindMeNote", found?.content)
	}

	@Test
	fun testUpdate() = runBlocking {
		noteList.clear()
		val note = HabitNoteEntity(3, 1, "OldNote", "2025-08-19T12:00")
		dao.insert(note)
		dao.update(HabitNoteEntity(3, 1, "UpdatedNote", "2025-08-19T12:00"))
		val updated = dao.getById(3)
		assertEquals("UpdatedNote", updated?.content)
	}

	@Test
	fun testDelete() = runBlocking {
		noteList.clear()
		val note = HabitNoteEntity(4, 1, "DeleteMeNote", "2025-08-19T13:00")
		dao.insert(note)
		dao.delete(note)
		assertNull(dao.getById(4))
	}

	@Test
	fun testDeleteAll() = runBlocking {
		noteList.clear()
		dao.insert(HabitNoteEntity(5, 2, "A", "2025-08-19T14:00"))
		dao.insert(HabitNoteEntity(6, 2, "B", "2025-08-19T15:00"))
		dao.deleteAll()
		assertTrue(noteList.isEmpty())
    }
// ...existing code...
}
