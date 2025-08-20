package manager

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
        val goal = AbstinenceGoalEntity(0, "No sugar", "Avoid sugar for 30 days")
        val id = manager.addAbstinenceGoal(goal)
        val fetched = manager.getAbstinenceGoalById(id)
        assertNotNull(fetched)
        assertEquals("No sugar", fetched?.name)
    }

    @Test
    fun testAddAndGetAddictionHabit() = runBlocking {
        val habit = AddictionHabitEntity(0, "Smoking", "Smoke less")
        val id = manager.addAddictionHabit(habit)
        val fetched = manager.getAddictionHabitById(id)
        assertNotNull(fetched)
        assertEquals("Smoking", fetched?.name)
    }

    @Test
    fun testAddAndGetAddictionNote() = runBlocking {
        val note = AddictionNoteEntity(0, "First note", "This is a note.")
        val id = manager.addAddictionNote(note)
        val fetched = manager.getAddictionNoteById(id)
        assertNotNull(fetched)
        assertEquals("First note", fetched?.title)
    }

    @Test
    fun testDeleteAllAbstinenceGoals() = runBlocking {
        manager.addAbstinenceGoal(AbstinenceGoalEntity(0, "No caffeine", "Avoid caffeine"))
        manager.addAbstinenceGoal(AbstinenceGoalEntity(0, "No alcohol", "Avoid alcohol"))
        manager.deleteAllAbstinenceGoals()
        assertTrue(manager.getAllAbstinenceGoals().isEmpty())
    }

    @Test
    fun testDeleteAllAddictionHabits() = runBlocking {
        manager.addAddictionHabit(AddictionHabitEntity(0, "Drinking", "Drink less"))
        manager.addAddictionHabit(AddictionHabitEntity(0, "Gambling", "Gamble less"))
        manager.deleteAllAddictionHabits()
        assertTrue(manager.getAllAddictionHabits().isEmpty())
    }

    @Test
    fun testDeleteAllAddictionNotes() = runBlocking {
        manager.addAddictionNote(AddictionNoteEntity(0, "Note 1", "Content 1"))
        manager.addAddictionNote(AddictionNoteEntity(0, "Note 2", "Content 2"))
        manager.deleteAllAddictionNotes()
        assertTrue(manager.getAllAddictionNotes().isEmpty())
    }
}
