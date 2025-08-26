package repository

import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.repository.AbstinenceGoalRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeAbstinenceGoalDao : com.example.rewire.db.dao.AbstinenceGoalDao {
    private val goals = mutableListOf<AbstinenceGoalEntity>()
    private var idCounter = 1L
    override suspend fun getAll(): List<AbstinenceGoalEntity> = goals.toList()
    override suspend fun getById(id: Long): AbstinenceGoalEntity? = goals.find { it.id == id }
    override suspend fun insert(goal: AbstinenceGoalEntity): Long {
        val newGoal = goal.copy(id = idCounter++)
        goals.add(newGoal)
        return newGoal.id
    }
    override suspend fun update(goal: AbstinenceGoalEntity) {
        val idx = goals.indexOfFirst { it.id == goal.id }
        if (idx != -1) goals[idx] = goal
    }
    override suspend fun delete(goal: AbstinenceGoalEntity) {
        goals.removeIf { it.id == goal.id }
    }
    override suspend fun deleteAll() {
        goals.clear()
    }
}

class AbstinenceGoalRepositoryTest {
    private lateinit var repo: AbstinenceGoalRepository
    private lateinit var dao: FakeAbstinenceGoalDao

    @Before
    fun setup() {
        dao = FakeAbstinenceGoalDao()
        repo = AbstinenceGoalRepository(dao)
    }

    @Test
    fun testInsertAndGetById() = runBlocking {
        val goal = AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Daily, value = 30, repeatCount = 1)
        val id = repo.insert(goal)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals(30, fetched?.value)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Daily, value = 14, repeatCount = 1))
        repo.insert(AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Weekly, value = 60, repeatCount = 2))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Daily, value = 7, repeatCount = 1))
        val updated = AbstinenceGoalEntity(id = id, addictionId = 1L, recurrence = RecurrenceType.MonthlyByDate(1), value = 14, repeatCount = 2)
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals(14, fetched?.value)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AbstinenceGoalEntity(0, 1L, RecurrenceType.Daily, 21, 1))
        val goal = repo.getById(id)!!
        repo.delete(goal)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Daily, value = 10, repeatCount = 1))
        repo.insert(AbstinenceGoalEntity(id = 0, addictionId = 1L, recurrence = RecurrenceType.Weekly, value = 10, repeatCount = 2))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
