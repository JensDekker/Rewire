package repository

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
        val goal = AbstinenceGoalEntity(0, "No sugar", "Avoid sugar for 30 days")
        val id = repo.insert(goal)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals("No sugar", fetched?.name)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AbstinenceGoalEntity(0, "No caffeine", "Avoid caffeine for 14 days"))
        repo.insert(AbstinenceGoalEntity(0, "No alcohol", "Avoid alcohol for 60 days"))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AbstinenceGoalEntity(0, "No TV", "Avoid TV for 7 days"))
        val updated = AbstinenceGoalEntity(id, "No TV", "Avoid TV for 14 days")
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals("Avoid TV for 14 days", fetched?.description)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AbstinenceGoalEntity(0, "No games", "Avoid games for 21 days"))
        val goal = repo.getById(id)!!
        repo.delete(goal)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AbstinenceGoalEntity(0, "No sweets", "Avoid sweets for 10 days"))
        repo.insert(AbstinenceGoalEntity(0, "No soda", "Avoid soda for 10 days"))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
