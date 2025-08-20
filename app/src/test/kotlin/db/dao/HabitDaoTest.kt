import com.example.rewire.db.entity.HabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.example.rewire.db.dao.HabitDao

class HabitDaoTest {
	private val habitList = mutableListOf<HabitEntity>()
	private val dao = object : HabitDao {
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
	}

	@Test
	fun testInsertAndGetAll() = runBlocking {
		habitList.clear()
		val habit = HabitEntity(1, "Test", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01")
		dao.insert(habit)
		val all = dao.getAll()
		assertEquals(1, all.size)
		assertEquals("Test", all[0].name)
	}

	@Test
	fun testGetById() = runBlocking {
		habitList.clear()
		val habit = HabitEntity(2, "FindMe", com.example.rewire.core.RecurrenceType.Daily, "09:00", 15, "2025-08-10")
		dao.insert(habit)
		val found = dao.getById(2)
		assertNotNull(found)
		assertEquals("FindMe", found?.name)
	}

	@Test
	fun testUpdate() = runBlocking {
		habitList.clear()
		val habit = HabitEntity(3, "Old", com.example.rewire.core.RecurrenceType.Daily, "10:00", 20, "2025-08-15")
		dao.insert(habit)
		dao.update(HabitEntity(3, "Updated", com.example.rewire.core.RecurrenceType.Daily, "10:00", 20, "2025-08-15"))
		val updated = dao.getById(3)
		assertEquals("Updated", updated?.name)
	}

	@Test
	fun testDelete() = runBlocking {
		habitList.clear()
		val habit = HabitEntity(4, "DeleteMe", com.example.rewire.core.RecurrenceType.Daily, "11:00", 25, "2025-08-16")
		dao.insert(habit)
		dao.delete(habit)
		assertNull(dao.getById(4))
	}

	@Test
	fun testDeleteAll() = runBlocking {
		habitList.clear()
		dao.insert(HabitEntity(5, "A", com.example.rewire.core.RecurrenceType.Daily, "12:00", 30, "2025-08-17"))
		dao.insert(HabitEntity(6, "B", com.example.rewire.core.RecurrenceType.Daily, "13:00", 35, "2025-08-18"))
		dao.deleteAll()
		assertTrue(habitList.isEmpty())
	}
}
// ...existing code...

// ...existing code...
