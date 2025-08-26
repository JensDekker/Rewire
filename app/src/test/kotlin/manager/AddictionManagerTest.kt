package manager

import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AddictionNoteEntity
import com.example.rewire.manager.AddictionManager
import com.example.rewire.repository.AbstinenceGoalRepository
import com.example.rewire.repository.AddictionHabitRepository
import com.example.rewire.repository.AddictionNoteRepository
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

class AddictionManagerTest {
    private lateinit var manager: AddictionManager

    @Before
    fun setup() {
        val abstinenceGoalRepo = AbstinenceGoalRepository(FakeAbstinenceGoalDao())
        val addictionHabitRepo = AddictionHabitRepository(FakeAddictionHabitDao())
        val addictionNoteRepo = AddictionNoteRepository(FakeAddictionNoteDao())
        manager = AddictionManager(abstinenceGoalRepo, addictionHabitRepo, addictionNoteRepo)
    }

    @Test
    fun testAddAndGetAbstinenceGoal() = runBlocking {
        val goal = AbstinenceGoalEntity(
            id = 0,
            addictionId = 1L,
            recurrence = RecurrenceType.Daily,
            value = 30,
            repeatCount = 1
        )
        val id = manager.addAbstinenceGoal(goal)
        val fetched = manager.getAbstinenceGoalById(id)
        assertNotNull(fetched)
        assertEquals(30, fetched?.value)
    }

    @Test
    fun testAddAndGetAddictionHabit() = runBlocking {
        val habit = AddictionHabitEntity(
            id = 0,
            name = "Smoking",
            startDate = "2025-08-20",
            recurrence = RecurrenceType.Daily,
            preferredTime = "08:00",
            estimatedMinutes = 10
        )
        val id = manager.addAddictionHabit(habit)
        val fetched = manager.getAddictionHabitById(id)
        assertNotNull(fetched)
        assertEquals("Smoking", fetched?.name)
    }

    @Test
    fun testAddAndGetAddictionNote() = runBlocking {
        val note = AddictionNoteEntity(
            id = 0,
            addictionId = 1L,
            content = "First note",
            timestamp = "2025-08-20T09:00:00"
        )
        val id = manager.addAddictionNote(note)
        val fetched = manager.getAddictionNoteById(id)
        assertNotNull(fetched)
        assertEquals("First note", fetched?.content)
    }

    @Test
    fun testDeleteAllAbstinenceGoals() = runBlocking {
        manager.addAbstinenceGoal(AbstinenceGoalEntity(0, 1L, RecurrenceType.Daily, 14, 1))
        manager.addAbstinenceGoal(AbstinenceGoalEntity(0, 1L, RecurrenceType.Weekly, 60, 2))
        manager.deleteAllAbstinenceGoals()
        assertTrue(manager.getAllAbstinenceGoals().isEmpty())
    }

    @Test
    fun testDeleteAllAddictionHabits() = runBlocking {
        manager.addAddictionHabit(AddictionHabitEntity(0, "Drinking", "2025-08-20", RecurrenceType.Daily, "09:00", 15))
        manager.addAddictionHabit(AddictionHabitEntity(0, "Gambling", "2025-08-20", RecurrenceType.Weekly, "10:00", 20))
        manager.deleteAllAddictionHabits()
        assertTrue(manager.getAllAddictionHabits().isEmpty())
    }

    @Test
    fun testDeleteAllAddictionNotes() = runBlocking {
        manager.addAddictionNote(AddictionNoteEntity(0, 1L, "Note 1", "2025-08-20T09:00:00"))
        manager.addAddictionNote(AddictionNoteEntity(0, 1L, "Note 2", "2025-08-20T10:00:00"))
        manager.deleteAllAddictionNotes()
        assertTrue(manager.getAllAddictionNotes().isEmpty())
    }
}
