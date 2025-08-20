package repository

import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.repository.AddictionHabitRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class FakeAddictionHabitDao : com.example.rewire.db.dao.AddictionHabitDao {
    private val habits = mutableListOf<AddictionHabitEntity>()
    private var idCounter = 1L
    override suspend fun getAll(): List<AddictionHabitEntity> = habits.toList()
    override suspend fun getById(id: Long): AddictionHabitEntity? = habits.find { it.id == id }
    override suspend fun insert(addictionHabit: AddictionHabitEntity): Long {
        val newHabit = addictionHabit.copy(id = idCounter++)
        habits.add(newHabit)
        return newHabit.id
    }
    override suspend fun update(addictionHabit: AddictionHabitEntity) {
        val idx = habits.indexOfFirst { it.id == addictionHabit.id }
        if (idx != -1) habits[idx] = addictionHabit
    }
    override suspend fun delete(addictionHabit: AddictionHabitEntity) {
        habits.removeIf { it.id == addictionHabit.id }
    }
    override suspend fun deleteAll() {
        habits.clear()
    }
}

class AddictionHabitRepositoryTest {
    private lateinit var repo: AddictionHabitRepository
    private lateinit var dao: FakeAddictionHabitDao

    @Before
    fun setup() {
        dao = FakeAddictionHabitDao()
        repo = AddictionHabitRepository(dao)
    }

    @Test
    fun testInsertAndGetById() = runBlocking {
        val habit = AddictionHabitEntity(0, "Smoking", "Smoke less")
        val id = repo.insert(habit)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals("Smoking", fetched?.name)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AddictionHabitEntity(0, "Drinking", "Drink less"))
        repo.insert(AddictionHabitEntity(0, "Gambling", "Gamble less"))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AddictionHabitEntity(0, "Gaming", "Game less"))
        val updated = AddictionHabitEntity(id, "Gaming", "Game responsibly")
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals("Game responsibly", fetched?.description)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AddictionHabitEntity(0, "Shopping", "Shop less"))
        val habit = repo.getById(id)!!
        repo.delete(habit)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AddictionHabitEntity(0, "Overeating", "Eat less"))
        repo.insert(AddictionHabitEntity(0, "Procrastination", "Procrastinate less"))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
