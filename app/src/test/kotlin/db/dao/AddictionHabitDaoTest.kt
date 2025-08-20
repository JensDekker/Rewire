import com.example.rewire.db.entity.AddictionHabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.example.rewire.db.dao.AddictionHabitDao
import com.example.rewire.core.RecurrenceType

class AddictionHabitDaoTest {
	private val habitList = mutableListOf<AddictionHabitEntity>()
	private val dao = object : AddictionHabitDao {
		override suspend fun getAll(): List<AddictionHabitEntity> = habitList
		override suspend fun getById(id: Long): AddictionHabitEntity? = habitList.find { it.id == id }
		override suspend fun insert(addictionHabit: AddictionHabitEntity): Long {
			habitList.add(addictionHabit)
			return addictionHabit.id
		}
		override suspend fun update(addictionHabit: AddictionHabitEntity) {
			habitList.replaceAll { if (it.id == addictionHabit.id) addictionHabit else it }
		}
		override suspend fun delete(addictionHabit: AddictionHabitEntity) {
			habitList.removeIf { it.id == addictionHabit.id }
		}
		override suspend fun deleteAll() {
			habitList.clear()
		}
	}

	@Test
	fun testInsertAndGetAll() = runBlocking {
		habitList.clear()
	val habit = AddictionHabitEntity(1L, "TestAddiction", "2025-08-19", RecurrenceType.Daily, "08:00", 10)
		dao.insert(habit)
		val all = dao.getAll()
		assertEquals(1, all.size)
		assertEquals("TestAddiction", all[0].name)
	assertEquals(RecurrenceType.Daily, all[0].recurrence)
	assertEquals("08:00", all[0].preferredTime)
	assertEquals(10, all[0].estimatedMinutes)
	}

	@Test
	fun testGetById() = runBlocking {
		habitList.clear()
	val habit = AddictionHabitEntity(2L, "FindMeAddiction", "2025-08-20", RecurrenceType.Weekly, "09:00", 15)
		dao.insert(habit)
	val found = dao.getById(2L)
		assertNotNull(found)
		assertEquals("FindMeAddiction", found?.name)
	assertEquals(RecurrenceType.Weekly, found?.recurrence)
	assertEquals("09:00", found?.preferredTime)
	assertEquals(15, found?.estimatedMinutes)
	}

	@Test
	fun testUpdate() = runBlocking {
		habitList.clear()
	val habit = AddictionHabitEntity(3L, "OldAddiction", "2025-08-21", RecurrenceType.Daily, "10:00", 20)
	dao.insert(habit)
	dao.update(AddictionHabitEntity(3L, "UpdatedAddiction", "2025-08-21", RecurrenceType.Weekly, "11:00", 25))
	val updated = dao.getById(3L)
	assertEquals("UpdatedAddiction", updated?.name)
	assertEquals(RecurrenceType.Weekly, updated?.recurrence)
	assertEquals("11:00", updated?.preferredTime)
	assertEquals(25, updated?.estimatedMinutes)
	}

	@Test
	fun testDelete() = runBlocking {
		habitList.clear()
	val habit = AddictionHabitEntity(4L, "DeleteMeAddiction", "2025-08-22", RecurrenceType.Daily, "12:00", 30)
		dao.insert(habit)
		dao.delete(habit)
	assertNull(dao.getById(4L))
	}

	@Test
	fun testDeleteAll() = runBlocking {
		habitList.clear()
	dao.insert(AddictionHabitEntity(5L, "A", "2025-08-23", RecurrenceType.Daily, "13:00", 35))
	dao.insert(AddictionHabitEntity(6L, "B", "2025-08-24", RecurrenceType.Weekly, "14:00", 40))
		dao.deleteAll()
		assertTrue(habitList.isEmpty())
	}
}

// ...existing code...
