import com.example.rewire.db.entity.HabitCompletion
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import com.example.rewire.db.dao.HabitCompletionDao

class HabitCompletionDaoTest {
	private val completionList = mutableListOf<HabitCompletion>()
	private val dao = object : HabitCompletionDao {
		override suspend fun insertCompletion(completion: HabitCompletion) {
			completionList.add(completion)
		}
		override suspend fun deleteCompletion(habitId: Long, date: String) {
			completionList.removeIf { it.habitId == habitId && it.date == date }
		}
		override suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletion> =
			completionList.filter { it.habitId == habitId }
	}


	@Test
	fun testInsertCompletion() = runBlocking {
		completionList.clear()
		val completion = HabitCompletion(1L, 1L, "2025-08-19")
		dao.insertCompletion(completion)
		assertTrue(completionList.any { it.id == 1L && it.habitId == 1L && it.date == "2025-08-19" })
	}


	@Test
	fun testDeleteCompletion() = runBlocking {
		completionList.clear()
		val completion = HabitCompletion(2L, 2L, "2025-08-20")
		completionList.add(completion)
		dao.deleteCompletion(2L, "2025-08-20")
		assertFalse(completionList.any { it.id == 2L && it.habitId == 2L && it.date == "2025-08-20" })
	}


	@Test
	fun testGetCompletionsForHabit() = runBlocking {
		completionList.clear()
		val c1 = HabitCompletion(3L, 3L, "2025-08-21")
		val c2 = HabitCompletion(4L, 3L, "2025-08-22")
		completionList.addAll(listOf(c1, c2))
		val completions = dao.getCompletionsForHabit(3L)
		assertEquals(2, completions.size)
		assertTrue(completions.any { it.id == 3L && it.habitId == 3L && it.date == "2025-08-21" })
		assertTrue(completions.any { it.id == 4L && it.habitId == 3L && it.date == "2025-08-22" })
	}
}
