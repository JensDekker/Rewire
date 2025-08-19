package com.example.rewire.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.rewire.db.dao.*
import com.example.rewire.db.entity.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.launch
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class RewireDatabaseTest {

    @Test
    fun bulkInsertUpdateDeleteHabits() {
        runBlocking {
            val habits = (1..100).map {
                HabitEntity(
                    id = 20000L + it,
                    name = "Bulk Habit $it",
                    recurrence = com.example.rewire.core.RecurrenceType.Daily,
                    preferredTime = "08:00",
                    estimatedMinutes = 5,
                    startDate = "2025-08-01"
                )
            }
            habits.forEach { habitDao.insert(it) }
            val all = habitDao.getAll().filter { it.id in 20001L..20100L }
            assertEquals(100, all.size)
            val updated = all.map { it.copy(name = it.name + " Updated") }
            updated.forEach { habitDao.update(it) }
            val updatedAll = habitDao.getAll().filter { it.name.endsWith("Updated") }
            assertEquals(100, updatedAll.size)
            updatedAll.forEach { habitDao.delete(it) }
            assertTrue(habitDao.getAll().none { it.id in 20001L..20100L })
        }
    }

    @Test
    fun relationshipIntegrity_cascadeDeleteHabit() {
        runBlocking {
            val habit = HabitEntity(
                id = 30000L,
                name = "Cascade Parent",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val note = HabitNoteEntity(id = 30001L, habitId = 30000L, content = "Cascade Note", timestamp = "2025-08-08T12:00:00Z")
            habitNoteDao.insert(note)
            habitDao.delete(habit)
            val notes = habitNoteDao.getAll().filter { it.habitId == 30000L }
            assertTrue(notes.isEmpty())
        }
    }

    @Test
    fun relationshipIntegrity_cascadeDeleteAddiction() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 40000L,
                name = "Cascade Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            val goal = AbstinenceGoalEntity(id = 40001L, addictionId = 40000L, recurrence = com.example.rewire.core.RecurrenceType.Daily, value = 1, repeatCount = 1)
            abstinenceGoalDao.insert(goal)
            addictionHabitDao.delete(addiction)
            val goals = abstinenceGoalDao.getAll().filter { it.addictionId == 40000L }
            assertTrue(goals.isEmpty())
        }
    }

    @Test
    fun concurrency_insertUpdateHabits() {
        runBlocking {
            val habits = (1..10).map {
                HabitEntity(
                    id = 50000L + it,
                    name = "Concurrent Habit $it",
                    recurrence = com.example.rewire.core.RecurrenceType.Daily,
                    preferredTime = "08:00",
                    estimatedMinutes = 5,
                    startDate = "2025-08-01"
                )
            }
            habits.forEach { habitDao.insert(it) }
            val jobs = mutableListOf<kotlinx.coroutines.Job>()
            habits.forEach { habit ->
                val job: kotlinx.coroutines.Job = launch {
                    val updated: HabitEntity = habit.copy(name = habit.name + " Updated")
                    habitDao.update(updated)
                }
                jobs.add(job)
            }
            jobs.forEach { it.join() }
            val updatedAll = habitDao.getAll().filter { it.name.endsWith("Updated") }
            assertEquals(10, updatedAll.size)
        }
    }
    @Test
    fun recurrenceType_persistence_and_retrieval() {
        runBlocking {
            // Daily
            val habitDaily = HabitEntity(
                id = 900L,
                name = "Daily Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "08:00",
                estimatedMinutes = 5,
                startDate = "2025-08-01"
            )
            habitDao.insert(habitDaily)
            val loadedDaily = habitDao.getById(900L)
            assertTrue(loadedDaily?.recurrence is com.example.rewire.core.RecurrenceType.Daily)

            // Weekly
            val addictionWeekly = AddictionHabitEntity(
                id = 901L,
                name = "Weekly Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Weekly,
                preferredTime = "10:00",
                estimatedMinutes = 15
            )
            addictionHabitDao.insert(addictionWeekly)
            val loadedWeekly = addictionHabitDao.getById(901L)
            assertTrue(loadedWeekly?.recurrence is com.example.rewire.core.RecurrenceType.Weekly)

            // MonthlyByDate
            val habitMonthlyDate = HabitEntity(
                id = 902L,
                name = "MonthlyByDate Habit",
                recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(15),
                preferredTime = "12:00",
                estimatedMinutes = 20,
                startDate = "2025-08-01"
            )
            habitDao.insert(habitMonthlyDate)
            val loadedMonthlyDate = habitDao.getById(902L)
            assertTrue(loadedMonthlyDate?.recurrence is com.example.rewire.core.RecurrenceType.MonthlyByDate)
            assertEquals(15, (loadedMonthlyDate?.recurrence as com.example.rewire.core.RecurrenceType.MonthlyByDate).dayOfMonth)

            // MonthlyByWeekday
            val habitMonthlyWeekday = HabitEntity(
                id = 903L,
                name = "MonthlyByWeekday Habit",
                recurrence = com.example.rewire.core.RecurrenceType.MonthlyByWeekday(2, com.example.rewire.core.DayOfWeek.FRIDAY),
                preferredTime = "14:00",
                estimatedMinutes = 25,
                startDate = "2025-08-01"
            )
            habitDao.insert(habitMonthlyWeekday)
            val loadedMonthlyWeekday = habitDao.getById(903L)
            assertTrue(loadedMonthlyWeekday?.recurrence is com.example.rewire.core.RecurrenceType.MonthlyByWeekday)
            val monthlyWeekday = loadedMonthlyWeekday?.recurrence as com.example.rewire.core.RecurrenceType.MonthlyByWeekday
            assertEquals(2, monthlyWeekday.weekOfMonth)
            assertEquals(com.example.rewire.core.DayOfWeek.FRIDAY, monthlyWeekday.dayOfWeek)

            // QuarterlyByDate
            val addictionQuarterlyDate = AddictionHabitEntity(
                id = 904L,
                name = "QuarterlyByDate Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.QuarterlyByDate(5, 0),
                preferredTime = "16:00",
                estimatedMinutes = 30
            )
            addictionHabitDao.insert(addictionQuarterlyDate)
            val loadedQuarterlyDate = addictionHabitDao.getById(904L)
            assertTrue(loadedQuarterlyDate?.recurrence is com.example.rewire.core.RecurrenceType.QuarterlyByDate)
            val quarterlyDate = loadedQuarterlyDate?.recurrence as com.example.rewire.core.RecurrenceType.QuarterlyByDate
            assertEquals(5, quarterlyDate.dayOfMonth)
            assertEquals(0, quarterlyDate.monthOffset)

            // QuarterlyByWeekday
            val addictionQuarterlyWeekday = AddictionHabitEntity(
                id = 905L,
                name = "QuarterlyByWeekday Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.QuarterlyByWeekday(1, com.example.rewire.core.DayOfWeek.MONDAY, 1),
                preferredTime = "18:00",
                estimatedMinutes = 35
            )
            addictionHabitDao.insert(addictionQuarterlyWeekday)
            val loadedQuarterlyWeekday = addictionHabitDao.getById(905L)
            assertTrue(loadedQuarterlyWeekday?.recurrence is com.example.rewire.core.RecurrenceType.QuarterlyByWeekday)
            val quarterlyWeekday = loadedQuarterlyWeekday?.recurrence as com.example.rewire.core.RecurrenceType.QuarterlyByWeekday
            assertEquals(1, quarterlyWeekday.weekOfMonth)
            assertEquals(com.example.rewire.core.DayOfWeek.MONDAY, quarterlyWeekday.dayOfWeek)
            assertEquals(1, quarterlyWeekday.monthOffset)

            // CustomWeekly
            val habitCustomWeekly = HabitEntity(
                id = 906L,
                name = "CustomWeekly Habit",
                recurrence = com.example.rewire.core.RecurrenceType.CustomWeekly(listOf(com.example.rewire.core.DayOfWeek.MONDAY, com.example.rewire.core.DayOfWeek.THURSDAY)),
                preferredTime = "20:00",
                estimatedMinutes = 40,
                startDate = "2025-08-01"
            )
            habitDao.insert(habitCustomWeekly)
            val loadedCustomWeekly = habitDao.getById(906L)
            assertTrue(loadedCustomWeekly?.recurrence is com.example.rewire.core.RecurrenceType.CustomWeekly)
            val customWeekly = loadedCustomWeekly?.recurrence as com.example.rewire.core.RecurrenceType.CustomWeekly
            assertEquals(listOf(com.example.rewire.core.DayOfWeek.MONDAY, com.example.rewire.core.DayOfWeek.THURSDAY), customWeekly.daysOfWeek)
        }
    }
    private lateinit var habitNoteDao: HabitNoteDao
    private lateinit var addictionNoteDao: AddictionNoteDao
    private lateinit var habitCompletionDao: HabitCompletionDao

    @Test
    fun updateParent_doesNotAffectNotesOrGoals() {
        runBlocking {
            val habit = HabitEntity(
                id = 400L,
                name = "Original",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val note = HabitNoteEntity(id = 4001L, habitId = 400L, content = "Note", timestamp = "2025-08-08T12:00:00Z")
            habitNoteDao.insert(note)
            val updatedHabit = habit.copy(name = "Changed")
            habitDao.update(updatedHabit)
            val notes = habitNoteDao.getAll().filter { it.habitId == 400L }
            assertEquals(1, notes.size)
            assertEquals("Note", notes[0].content)
        }
    }

    @Test(expected = Exception::class)
    fun insertHabitNoteWithNonexistentParent_throwsException() {
        runBlocking {
            val note = HabitNoteEntity(id = 5001L, habitId = 9999L, content = "Orphan", timestamp = "2025-08-08T12:00:00Z")
            habitNoteDao.insert(note)
        }
    }

    @Test
    fun bulkInsertUpdateDeleteHabitNotes() {
        runBlocking {
            val habit = HabitEntity(
                id = 600L,
                name = "Bulk",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val notes = (1..10).map {
                HabitNoteEntity(id = 6000L + it, habitId = 600L, content = "Bulk $it", timestamp = "2025-08-08T12:00:00Z")
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
    }

    @Test
    fun edgeCase_multipleParentTypes() {
        runBlocking {
            val habit = HabitEntity(
                id = 700L,
                name = "Edge",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            val addiction = AddictionHabitEntity(
                id = 701L,
                name = "EdgeAddiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 1
            )
            habitDao.insert(habit)
            addictionHabitDao.insert(addiction)
            val note1 = HabitNoteEntity(id = 7001L, habitId = 700L, content = "HabitNote", timestamp = "2025-08-08T12:00:00Z")
            val note2 = AddictionNoteEntity(id = 7002L, addictionId = 701L, content = "AddictionNote", timestamp = "2025-08-08T13:00:00Z")
            habitNoteDao.insert(note1)
            addictionNoteDao.insert(note2)
            val habitNotes = habitNoteDao.getAll().filter { it.habitId == 700L }
            val addictionNotes = addictionNoteDao.getAll().filter { it.addictionId == 701L }
            assertEquals(1, habitNotes.size)
            assertEquals(1, addictionNotes.size)
        }
    }

    @Test
    fun dateTimeFieldHandling() {
        runBlocking {
            val habit = HabitEntity(
                id = 800L,
                name = "DateTest",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            val note = HabitNoteEntity(id = 8001L, habitId = 800L, content = "DateNote", timestamp = "2025-08-08T15:30:00+02:00")
            habitNoteDao.insert(note)
            val loaded = habitNoteDao.getById(8001)
            assertNotNull(loaded)
            assertEquals("2025-08-08T15:30:00+02:00", loaded?.timestamp)
        }
    }
    @Test
    fun deletingAddiction_deletesAssociatedAbstinenceGoals() {
        runBlocking {
        // Insert an addiction habit
            val addiction = AddictionHabitEntity(id = 300L, name = "Cascade Addiction for Goal", startDate = "2025-08-01", recurrence = com.example.rewire.core.RecurrenceType.Daily, preferredTime = null, estimatedMinutes = null)
            addictionHabitDao.insert(addiction)
            // Insert abstinence goals for this addiction
            val goal1 = AbstinenceGoalEntity(id = 3001L, addictionId = 300L, recurrence = com.example.rewire.core.RecurrenceType.Daily, value = 1, repeatCount = 1)
            val goal2 = AbstinenceGoalEntity(id = 3002L, addictionId = 300L, recurrence = com.example.rewire.core.RecurrenceType.Weekly, value = 2, repeatCount = 2)
            abstinenceGoalDao.insert(goal1)
            abstinenceGoalDao.insert(goal2)
            // Delete the addiction
            addictionHabitDao.delete(addiction)
            // Abstinence goals should be deleted
            val goals = abstinenceGoalDao.getAll().filter { it.addictionId == 300L }
            assertTrue(goals.isEmpty())
        }
    }
    @Test
    fun deletingHabit_deletesAssociatedNotes() {
        runBlocking {
        // Insert a habit
            val habit = HabitEntity(
                id = 100L,
                name = "Cascade Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            // Insert notes for this habit
            val note1 = HabitNoteEntity(id = 1001L, habitId = 100L, content = "Cascade Note 1", timestamp = "2025-08-08T12:00:00Z")
            val note2 = HabitNoteEntity(id = 1002L, habitId = 100L, content = "Cascade Note 2", timestamp = "2025-08-08T13:00:00Z")
            habitNoteDao.insert(note1)
            habitNoteDao.insert(note2)
            // Delete the habit
            habitDao.delete(habit)
            // Notes should be deleted
            val notes = habitNoteDao.getAll().filter { it.habitId == 100L }
            assertTrue(notes.isEmpty())
        }
    }

    @Test
    fun deletingAddiction_deletesAssociatedNotes() {
        runBlocking {
        // Insert an addiction habit
            val addiction = AddictionHabitEntity(
                id = 200L,
                name = "Cascade Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            // Insert notes for this addiction
            val note1 = AddictionNoteEntity(id = 2001L, addictionId = 200L, content = "Cascade A Note 1", timestamp = "2025-08-08T14:00:00Z")
            addictionNoteDao.insert(note1)
            // Delete the addiction
            addictionHabitDao.delete(addiction)
            // Notes should be deleted
            val notes = addictionNoteDao.getAll().filter { it.addictionId == 200L }
            assertTrue(notes.isEmpty())
        }
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
        habitCompletionDao = db.habitCompletionDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun habitDao_crudOperations() {
        runBlocking {
            val habit = HabitEntity(
                id = 1L,
                name = "Test Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
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
    }

    @Test
    fun addictionHabitDao_crudOperations() {
        runBlocking {
            val addiction = AddictionHabitEntity(
                id = 1L,
                name = "Test Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Weekly,
                preferredTime = "09:00",
                estimatedMinutes = 3
            )
            val id = addictionHabitDao.insert(addiction)
            val loaded = addictionHabitDao.getById(id)
            assertNotNull(loaded)
            addictionHabitDao.delete(loaded!!)
            assertNull(addictionHabitDao.getById(id))
        }
    }

    @Test
    fun abstinenceGoalDao_crudOperations() {
        runBlocking {
            // Insert parent addiction first
            val addiction = AddictionHabitEntity(
                id = 1L,
                name = "Test Addiction",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1),
                preferredTime = "09:00",
                estimatedMinutes = 1
            )
            addictionHabitDao.insert(addiction)

            val goal = AbstinenceGoalEntity(id = 1L, addictionId = 1L, recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1), value = 1, repeatCount = 1)
            val id = abstinenceGoalDao.insert(goal)
            val loaded = abstinenceGoalDao.getById(id)
            assertNotNull(loaded)
            abstinenceGoalDao.delete(loaded!!)
            assertNull(abstinenceGoalDao.getById(id))
        }
    }

    @Test
    fun habitNoteDao_crudOperations() {
        runBlocking {
            // Insert parent habit first
            val habit = HabitEntity(
                id = 1L,
                name = "Test Habit",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)

            val note = HabitNoteEntity(id = 1L, habitId = 1L, content = "Test note", timestamp = "2025-08-08T12:00:00Z")
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
    }

    @Test
    fun habitNotes_relationship_with_habit() {
        runBlocking {
        // Insert a habit
            val habit = HabitEntity(
                id = 10L,
                name = "Habit with Notes",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 10,
                startDate = "2025-08-01"
            )
            habitDao.insert(habit)
            // Insert notes for this habit
            val note1 = HabitNoteEntity(id = 101L, habitId = 10L, content = "Note 1", timestamp = "2025-08-08T12:00:00Z")
            val note2 = HabitNoteEntity(id = 102L, habitId = 10L, content = "Note 2", timestamp = "2025-08-08T13:00:00Z")
            habitNoteDao.insert(note1)
            habitNoteDao.insert(note2)
            // Query all notes for this habit
            val notes = habitNoteDao.getAll().filter { it.habitId == 10L }
            assertEquals(2, notes.size)
            assertTrue(notes.any { it.content == "Note 1" })
            assertTrue(notes.any { it.content == "Note 2" })
        }
    }

    @Test
    fun addictionNotes_relationship_with_addictionHabit() {
        runBlocking {
        // Insert an addiction habit
            val addiction = AddictionHabitEntity(
                id = 20L,
                name = "Addiction with Notes",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "09:00",
                estimatedMinutes = 2
            )
            addictionHabitDao.insert(addiction)
            // Insert notes for this addiction
            val note1 = AddictionNoteEntity(id = 201L, addictionId = 20L, content = "A Note 1", timestamp = "2025-08-08T14:00:00Z")
            addictionNoteDao.insert(note1)
            // Query all notes for this addiction
            val notes = addictionNoteDao.getAll().filter { it.addictionId == 20L }
            assertEquals(1, notes.size)
            assertEquals("A Note 1", notes[0].content)
        }
    }

    @Test
    fun abstinenceGoal_relationship_with_addictionHabit() {
        runBlocking {
        // Insert an addiction habit
            val addiction = AddictionHabitEntity(
                id = 30L,
                name = "Addiction for Goal",
                startDate = "2025-08-01",
                recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1),
                preferredTime = "09:00",
                estimatedMinutes = 1
            )
            addictionHabitDao.insert(addiction)
            // Insert an abstinence goal for this addiction
            val goal = AbstinenceGoalEntity(id = 301L, addictionId = 30L, recurrence = com.example.rewire.core.RecurrenceType.MonthlyByDate(1), value = 1, repeatCount = 1)
            abstinenceGoalDao.insert(goal)
            // Query all goals for this addiction
            val goals = abstinenceGoalDao.getAll().filter { it.addictionId == 30L }
            assertEquals(1, goals.size)
        }
    }

    @Test
    fun habitCompletionDao_crudOperations() = runBlocking {
        val habit = HabitEntity(
            id = 10000L,
            name = "Completion Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion = HabitCompletion(
            habitId = 10000L,
            date = "2025-08-18"
        )
        habitCompletionDao.insertCompletion(completion)
        val completions = habitCompletionDao.getCompletionsForHabit(10000L)
        assertEquals(1, completions.size)
        assertEquals("2025-08-18", completions[0].date)
        habitCompletionDao.deleteCompletion(10000L, "2025-08-18")
        val completionsAfterDelete = habitCompletionDao.getCompletionsForHabit(10000L)
        assertTrue(completionsAfterDelete.isEmpty())
    }
    @Test
    fun habitCompletionDao_queryByDate() = runBlocking {
        val habit = HabitEntity(
            id = 20000L,
            name = "Query Test Habit",
            recurrence = com.example.rewire.core.RecurrenceType.Daily,
            preferredTime = "07:00",
            estimatedMinutes = 5,
            startDate = "2025-08-01"
        )
        habitDao.insert(habit)
        val completion1 = HabitCompletion(habitId = 20000L, date = "2025-08-17")
        val completion2 = HabitCompletion(habitId = 20000L, date = "2025-08-18")
        habitCompletionDao.insertCompletion(completion1)
        habitCompletionDao.insertCompletion(completion2)
        val completions = habitCompletionDao.getCompletionsForHabit(20000L)
        assertEquals(2, completions.size)
        val dates = completions.map { it.date }
        assertTrue(dates.contains("2025-08-17"))
        assertTrue(dates.contains("2025-08-18"))
    }
}
