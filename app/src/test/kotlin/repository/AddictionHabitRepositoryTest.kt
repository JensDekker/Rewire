package repository

import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
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
        val habit = AddictionHabitEntity(
            id = 0,
            name = "Smoking",
            startDate = "2025-08-20",
            recurrence = RecurrenceType.Daily,
            preferredTime = "08:00",
            estimatedMinutes = 10
        )
        val id = repo.insert(habit)
        val fetched = repo.getById(id)
        assertNotNull(fetched)
        assertEquals("Smoking", fetched?.name)
    }

    @Test
    fun testGetAll() = runBlocking {
        repo.insert(AddictionHabitEntity(id = 0, name = "Drinking", startDate = "2025-08-20", recurrence = RecurrenceType.Daily, preferredTime = "09:00", estimatedMinutes = 15))
        repo.insert(AddictionHabitEntity(id = 0, name = "Gambling", startDate = "2025-08-20", recurrence = RecurrenceType.Weekly, preferredTime = "10:00", estimatedMinutes = 20))
        val all = repo.getAll()
        assertEquals(2, all.size)
    }

    @Test
    fun testUpdate() = runBlocking {
        val id = repo.insert(AddictionHabitEntity(id = 0, name = "Gaming", startDate = "2025-08-20", recurrence = RecurrenceType.Daily, preferredTime = "11:00", estimatedMinutes = 30))
        val updated = AddictionHabitEntity(id = id, name = "Gaming", startDate = "2025-08-20", recurrence = RecurrenceType.MonthlyByDate(1), preferredTime = "12:00", estimatedMinutes = 45)
        repo.update(updated)
        val fetched = repo.getById(id)
        assertEquals("Gaming", fetched?.name)
    }

    @Test
    fun testDelete() = runBlocking {
        val id = repo.insert(AddictionHabitEntity(0, "Shopping", "2025-08-20", RecurrenceType.Daily, "13:00", 25))
        val habit = repo.getById(id)!!
        repo.delete(habit)
        assertNull(repo.getById(id))
    }

    @Test
    fun testDeleteAll() = runBlocking {
        repo.insert(AddictionHabitEntity(id = 0, name = "Overeating", startDate = "2025-08-20", recurrence = RecurrenceType.Daily, preferredTime = "14:00", estimatedMinutes = 35))
        repo.insert(AddictionHabitEntity(id = 0, name = "Procrastination", startDate = "2025-08-20", recurrence = RecurrenceType.Weekly, preferredTime = "15:00", estimatedMinutes = 40))
        repo.deleteAll()
        assertTrue(repo.getAll().isEmpty())
    }
}
