import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.db.dao.AbstinenceGoalDao
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.example.rewire.core.RecurrenceType

class AbstinenceGoalDaoTest {
	private val goalList = mutableListOf<AbstinenceGoalEntity>()
	private val dao = object : AbstinenceGoalDao {
		override suspend fun getAll(): List<AbstinenceGoalEntity> = goalList
		override suspend fun getById(id: Long): AbstinenceGoalEntity? = goalList.find { it.id == id }
		override suspend fun insert(goal: AbstinenceGoalEntity): Long {
			goalList.add(goal)
			return goal.id
		}
		override suspend fun update(goal: AbstinenceGoalEntity) {
			goalList.replaceAll { if (it.id == goal.id) goal else it }
		}
		override suspend fun delete(goal: AbstinenceGoalEntity) {
			goalList.removeIf { it.id == goal.id }
		}
		override suspend fun deleteAll() {
			goalList.clear()
		}
	}

	@Test
	fun testInsertAndGetAll() = runBlocking {
		goalList.clear()
	val goal = AbstinenceGoalEntity(1L, 1L, RecurrenceType.Daily, 10, 1)
		dao.insert(goal)
		val all = dao.getAll()
		assertEquals(1, all.size)
	assertEquals(1L, all[0].addictionId)
	assertEquals(RecurrenceType.Daily, all[0].recurrence)
	assertEquals(10, all[0].value)
	assertEquals(1, all[0].repeatCount)
	}

	@Test
	fun testGetById() = runBlocking {
		goalList.clear()
	val goal = AbstinenceGoalEntity(2L, 2L, RecurrenceType.Weekly, 5, 2)
		dao.insert(goal)
	val found = dao.getById(2L)
		assertNotNull(found)
	assertEquals(2L, found?.addictionId)
	assertEquals(RecurrenceType.Weekly, found?.recurrence)
	assertEquals(5, found?.value)
	assertEquals(2, found?.repeatCount)
	}

	@Test
	fun testUpdate() = runBlocking {
		goalList.clear()
	val goal = AbstinenceGoalEntity(3L, 3L, RecurrenceType.Daily, 7, 3)
	dao.insert(goal)
	dao.update(AbstinenceGoalEntity(3L, 3L, RecurrenceType.Weekly, 8, 4))
	val updated = dao.getById(3L)
	assertEquals(RecurrenceType.Weekly, updated?.recurrence)
	assertEquals(8, updated?.value)
	assertEquals(4, updated?.repeatCount)
	}

	@Test
	fun testDelete() = runBlocking {
		goalList.clear()
	val goal = AbstinenceGoalEntity(4L, 4L, RecurrenceType.Daily, 9, 2)
		dao.insert(goal)
		dao.delete(goal)
	assertNull(dao.getById(4L))
	}

	@Test
	fun testDeleteAll() = runBlocking {
		goalList.clear()
	dao.insert(AbstinenceGoalEntity(5L, 5L, RecurrenceType.Daily, 10, 1))
	dao.insert(AbstinenceGoalEntity(6L, 6L, RecurrenceType.Weekly, 11, 2))
		dao.deleteAll()
		assertTrue(goalList.isEmpty())
	}
	}
