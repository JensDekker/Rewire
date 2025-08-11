package com.example.rewire.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.*
import com.example.rewire.db.entity.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class RewireDatabaseTest {
    private lateinit var habitNoteDao: HabitNoteDao
    private lateinit var addictionNoteDao: AddictionNoteDao

    @Test
    fun updateParent_doesNotAffectNotesOrGoals() = runBlocking {
        val habit = HabitEntity(
            id = 400,
            name = "Original",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val note = HabitNoteEntity(id = 4001, habitId = 400, content = "Note", timestamp = "2025-08-08T12:00:00Z")
        habitNoteDao.insert(note)
        val updatedHabit = habit.copy(name = "Changed")
        habitDao.update(updatedHabit)
        val notes = habitNoteDao.getAll().filter { it.habitId == 400L }
        assertEquals(1, notes.size)
        assertEquals("Note", notes[0].content)
    }

    @Test(expected = Exception::class)
    fun insertHabitNoteWithNonexistentParent_throwsException() = runBlocking {
        val note = HabitNoteEntity(id = 5001, habitId = 9999, content = "Orphan", timestamp = "2025-08-08T12:00:00Z")
        habitNoteDao.insert(note)
    }

    @Test
    fun bulkInsertUpdateDeleteHabitNotes() = runBlocking {
        val habit = HabitEntity(
            id = 600,
            name = "Bulk",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val notes = (1..10).map {
            HabitNoteEntity(id = 6000 + it, habitId = 600, content = "Bulk $it", timestamp = "2025-08-08T12:00:00Z")
        }
        notes.forEach { habitNoteDao.insert(it) }
        val allNotes = habitNoteDao.getAll().filter { it.habitId == 600L }
        assertEquals(10, allNotes.size)
        val updated = allNotes.map { it.copy(content = it.content + " updated") }
        updated.forEach { habitNoteDao.update(it) }
        val updatedNotes = habitNoteDao.getAll().filter { it.habitId == 600L && it.content.endsWith("updated") }
        assertEquals(10, updatedNotes.size)
        updatedNotes.forEach { habitNoteDao.delete(it) }
        assertTrue(habitNoteDao.getAll().none { it.habitId == 600L })
    }

    @Test
    fun edgeCase_multipleParentTypes() = runBlocking {
        val habit = HabitEntity(
            id = 700,
            name = "Edge",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        val addiction = AddictionHabitEntity(
            id = 701,
            name = "EdgeAddiction",
            startDate = "2025-08-01",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 1
        )
        habitDao.insert(habit)
        addictionHabitDao.insert(addiction)
        val note1 = HabitNoteEntity(id = 7001, habitId = 700, content = "HabitNote", timestamp = "2025-08-08T12:00:00Z")
        val note2 = AddictionNoteEntity(id = 7002, addictionId = 701, content = "AddictionNote", timestamp = "2025-08-08T13:00:00Z")
        habitNoteDao.insert(note1)
        addictionNoteDao.insert(note2)
        val habitNotes = habitNoteDao.getAll().filter { it.habitId == 700L }
        val addictionNotes = addictionNoteDao.getAll().filter { it.addictionId == 701L }
        assertEquals(1, habitNotes.size)
        assertEquals(1, addictionNotes.size)
    }

    @Test
    fun dateTimeFieldHandling() = runBlocking {
        val habit = HabitEntity(
            id = 800,
            name = "DateTest",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val note = HabitNoteEntity(id = 8001, habitId = 800, content = "DateNote", timestamp = "2025-08-08T15:30:00+02:00")
        habitNoteDao.insert(note)
        val loaded = habitNoteDao.getById(8001)
        assertNotNull(loaded)
        assertEquals("2025-08-08T15:30:00+02:00", loaded?.timestamp)
    }
    @Test
    fun deletingAddiction_deletesAssociatedAbstinenceGoals() = runBlocking {
        // Insert an addiction habit
        val addiction = AddictionHabitEntity(id = 300, name = "Cascade Addiction for Goal", startDate = "2025-08-01", recurrence = "daily", preferredTime = null, estimatedMinutes = null)
        addictionHabitDao.insert(addiction)
        // Insert abstinence goals for this addiction
        val goal1 = AbstinenceGoalEntity(id = 3001, addictionId = 300, recurrence = "daily", value = 1, repeatCount = 1)
        val goal2 = AbstinenceGoalEntity(id = 3002, addictionId = 300, recurrence = "weekly", value = 2, repeatCount = 2)
        abstinenceGoalDao.insert(goal1)
        abstinenceGoalDao.insert(goal2)
        // Delete the addiction
        addictionHabitDao.delete(addiction)
        // Abstinence goals should be deleted
        val goals = abstinenceGoalDao.getAll().filter { it.addictionId == 300L }
        assertTrue(goals.isEmpty())
    }
    @Test
    fun deletingHabit_deletesAssociatedNotes() = runBlocking {
        // Insert a habit
        val habit = HabitEntity(
            id = 100,
            name = "Cascade Habit",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        // Insert notes for this habit
        val note1 = HabitNoteEntity(id = 1001, habitId = 100, content = "Cascade Note 1", timestamp = "2025-08-08T12:00:00Z")
        val note2 = HabitNoteEntity(id = 1002, habitId = 100, content = "Cascade Note 2", timestamp = "2025-08-08T13:00:00Z")
        habitNoteDao.insert(note1)
        habitNoteDao.insert(note2)
        // Delete the habit
        habitDao.delete(habit)
        // Notes should be deleted
        val notes = habitNoteDao.getAll().filter { it.habitId == 100L }
        assertTrue(notes.isEmpty())
    }

    @Test
    fun deletingAddiction_deletesAssociatedNotes() = runBlocking {
        // Insert an addiction habit
        val addiction = AddictionHabitEntity(
            id = 200,
            name = "Cascade Addiction",
            startDate = "2025-08-01",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 2
        )
        addictionHabitDao.insert(addiction)
        // Insert notes for this addiction
        val note1 = AddictionNoteEntity(id = 2001, addictionId = 200, content = "Cascade A Note 1", timestamp = "2025-08-08T14:00:00Z")
        addictionNoteDao.insert(note1)
        // Delete the addiction
        addictionHabitDao.delete(addiction)
        // Notes should be deleted
        val notes = addictionNoteDao.getAll().filter { it.addictionId == 200L }
        assertTrue(notes.isEmpty())
    }
    private lateinit var db: RewireDatabase
    private lateinit var habitDao: HabitDao
    private lateinit var addictionHabitDao: AddictionHabitDao
    private lateinit var abstinenceGoalDao: AbstinenceGoalDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, RewireDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        habitDao = db.habitDao()
        addictionHabitDao = db.addictionHabitDao()
        abstinenceGoalDao = db.abstinenceGoalDao()
        habitNoteDao = db.habitNoteDao()
        addictionNoteDao = db.addictionNoteDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitDao_crudOperations() = runBlocking {
        val habit = HabitEntity(
            id = 1,
            name = "Test Habit",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        val id = habitDao.insert(habit)
        val loaded = habitDao.getById(id)
        assertNotNull(loaded)
        assertEquals("Test Habit", loaded?.name)
        val updated = loaded!!.copy(name = "Updated Habit")
        habitDao.update(updated)
        val loaded2 = habitDao.getById(id)
        assertEquals("Updated Habit", loaded2?.name)
        habitDao.delete(updated)
        assertNull(habitDao.getById(id))
    }

    @Test
    fun addictionHabitDao_crudOperations() = runBlocking {
        val addiction = AddictionHabitEntity(
            id = 1,
            name = "Test Addiction",
            startDate = "2025-08-01",
            recurrence = "weekly",
            preferredTime = "09:00",
            estimatedMinutes = 3
        )
        val id = addictionHabitDao.insert(addiction)
        val loaded = addictionHabitDao.getById(id)
        assertNotNull(loaded)
        assertEquals(3, loaded?.allowedUses)
        val updated = loaded!!.copy(allowedUses = 5)
        addictionHabitDao.update(updated)
        val loaded2 = addictionHabitDao.getById(id)
        assertEquals(5, loaded2?.allowedUses)
        addictionHabitDao.delete(updated)
        assertNull(addictionHabitDao.getById(id))
    }

    @Test
    fun abstinenceGoalDao_crudOperations() = runBlocking {
        val goal = AbstinenceGoalEntity(id = 1, addictionId = 1, startDate = "2025-01-01", endDate = "2025-02-01")
        val id = abstinenceGoalDao.insert(goal)
        val loaded = abstinenceGoalDao.getById(id)
        assertNotNull(loaded)
        assertEquals("2025-01-01", loaded?.startDate)
        abstinenceGoalDao.delete(loaded!!)
        assertNull(abstinenceGoalDao.getById(id))
    }

    @Test
    fun habitNoteDao_crudOperations() = runBlocking {
        val note = HabitNoteEntity(id = 1, habitId = 1, content = "Test note", timestamp = "2025-08-08T12:00:00Z")
        val id = habitNoteDao.insert(note)
        val loaded = habitNoteDao.getById(id)
        assertNotNull(loaded)
        assertEquals("Test note", loaded?.content)
        val updated = loaded!!.copy(content = "Updated note")
        habitNoteDao.update(updated)
        val loaded2 = habitNoteDao.getById(id)
        assertEquals("Updated note", loaded2?.content)
        habitNoteDao.delete(updated)
        assertNull(habitNoteDao.getById(id))
    }

    @Test
    fun habitNotes_relationship_with_habit() = runBlocking {
        // Insert a habit
        val habit = HabitEntity(
            id = 10,
            name = "Habit with Notes",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 10,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        // Insert notes for this habit
        val note1 = HabitNoteEntity(id = 101, habitId = 10, content = "Note 1", timestamp = "2025-08-08T12:00:00Z")
        val note2 = HabitNoteEntity(id = 102, habitId = 10, content = "Note 2", timestamp = "2025-08-08T13:00:00Z")
        habitNoteDao.insert(note1)
        habitNoteDao.insert(note2)
        // Query all notes for this habit
        val notes = habitNoteDao.getAll().filter { it.habitId == 10L }
        assertEquals(2, notes.size)
        assertTrue(notes.any { it.content == "Note 1" })
        assertTrue(notes.any { it.content == "Note 2" })
    }

    @Test
    fun addictionNotes_relationship_with_addictionHabit() = runBlocking {
        // Insert an addiction habit
        val addiction = AddictionHabitEntity(
            id = 20,
            name = "Addiction with Notes",
            startDate = "2025-08-01",
            recurrence = "daily",
            preferredTime = "09:00",
            estimatedMinutes = 2
        )
        addictionHabitDao.insert(addiction)
        // Insert notes for this addiction
        val note1 = AddictionNoteEntity(id = 201, addictionId = 20, content = "A Note 1", timestamp = "2025-08-08T14:00:00Z")
        addictionNoteDao.insert(note1)
        // Query all notes for this addiction
        val notes = addictionNoteDao.getAll().filter { it.addictionId == 20L }
        assertEquals(1, notes.size)
        assertEquals("A Note 1", notes[0].content)
    }

    @Test
    fun abstinenceGoal_relationship_with_addictionHabit() = runBlocking {
        // Insert an addiction habit
        val addiction = AddictionHabitEntity(
            id = 30,
            name = "Addiction for Goal",
            startDate = "2025-08-01",
            recurrence = "monthly",
            preferredTime = "09:00",
            estimatedMinutes = 1
        )
        addictionHabitDao.insert(addiction)
        // Insert an abstinence goal for this addiction
        val goal = AbstinenceGoalEntity(id = 301, addictionId = 30, startDate = "2025-08-01", endDate = "2025-08-31")
        abstinenceGoalDao.insert(goal)
        // Query all goals for this addiction
        val goals = abstinenceGoalDao.getAll().filter { it.addictionId == 30L }
        assertEquals(1, goals.size)
        assertEquals("2025-08-01", goals[0].startDate)
    }
}
