package com.example.rewire.repository

import com.example.rewire.db.entity.HabitEntity
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class HabitRepositoryTest {
    @Test
    fun testGetAllHabits_empty() = runBlocking {
        habitList.clear()
        val all = repo.getAllHabits()
        assertTrue(all.isEmpty())
    }

    @Test
    fun testGetHabitById_invalid() = runBlocking {
        habitList.clear()
        val habit = repo.getHabitById(999)
        assertNull(habit)
    }

    @Test
    fun testInsertHabit_duplicate() = runBlocking {
        habitList.clear()
    val habit = HabitEntity(1L, "Dup", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01")
    repo.insertHabit(habit)
    repo.insertHabit(habit)
    assertEquals(2, habitList.count { it.id == 1L })
    }
    // In-memory lists to simulate DB
    private val habitList = mutableListOf<HabitEntity>()
    private val completionList = mutableListOf<com.example.rewire.db.entity.HabitCompletion>()


    // Fake DAO matching Room signatures
    private val dao = object : com.example.rewire.db.dao.HabitDao {
        override suspend fun getAll(): List<HabitEntity> = habitList
        override suspend fun getById(id: Long): HabitEntity? = habitList.find { it.id == id }
        override suspend fun insert(habit: HabitEntity): Long {
            habitList.add(habit)
            return habit.id
        }
        override suspend fun update(habit: HabitEntity) {
            habitList.replaceAll { if (it.id == habit.id) habit else it }
        }
        override suspend fun delete(habit: HabitEntity) {
            habitList.removeIf { it.id == habit.id }
        }
        override suspend fun deleteAll() {
            habitList.clear()
        }
    }

    // Fake Completion DAO matching Room signatures
    private val completionDao = object : com.example.rewire.db.dao.HabitCompletionDao {
        override suspend fun insertCompletion(completion: com.example.rewire.db.entity.HabitCompletion) {
            completionList.add(completion)
        }
        override suspend fun deleteCompletion(habitId: Long, date: String) {
            completionList.removeIf { it.habitId == habitId && it.date == date }
        }
        override suspend fun getCompletionsForHabit(habitId: Long): List<com.example.rewire.db.entity.HabitCompletion> =
            completionList.filter { it.habitId == habitId }
    }

    private val repo = com.example.rewire.repository.HabitRepository(dao).apply {
        setHabitCompletionDao(completionDao)
    }

    @Test
    fun testGetAllHabits() = runBlocking {
        habitList.clear()
        habitList.add(HabitEntity(1, "Test", com.example.rewire.core.RecurrenceType.Daily, "08:00", 10, "2025-08-01"))
        val all = repo.getAllHabits()
        assertEquals(1, all.size)
    }

    @Test
    fun testGetHabitById() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(2L, "FindMe", com.example.rewire.core.RecurrenceType.Daily, "09:00", 15, "2025-08-10"))
    val habit = repo.getHabitById(2L)
        assertNotNull(habit)
        assertEquals("FindMe", habit?.name)
    }

    @Test
    fun testInsertHabit() = runBlocking {
        habitList.clear()
    repo.insertHabit(HabitEntity(3L, "InsertMe", com.example.rewire.core.RecurrenceType.Daily, "10:00", 20, "2025-08-15"))
    assertTrue(habitList.any { it.name == "InsertMe" })
    }

    @Test
    fun testUpdateHabit() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(4L, "Old", com.example.rewire.core.RecurrenceType.Daily, "11:00", 25, "2025-08-16"))
    repo.updateHabit(HabitEntity(4L, "Updated", com.example.rewire.core.RecurrenceType.Daily, "11:00", 25, "2025-08-16"))
    assertEquals("Updated", habitList.find { it.id == 4L }?.name)
    }

    @Test
    fun testDeleteHabit() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(5L, "DeleteMe", com.example.rewire.core.RecurrenceType.Daily, "12:00", 30, "2025-08-17"))
    repo.deleteHabit(HabitEntity(5L, "DeleteMe", com.example.rewire.core.RecurrenceType.Daily, "12:00", 30, "2025-08-17"))
    assertFalse(habitList.any { it.id == 5L })
    }

    @Test
    fun testGetHabitsByRecurrence() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(6L, "Daily", com.example.rewire.core.RecurrenceType.Daily, "13:00", 35, "2025-08-18"))
    habitList.add(HabitEntity(7L, "Weekly", com.example.rewire.core.RecurrenceType.Weekly, "14:00", 40, "2025-08-19"))
    val daily = repo.getHabitsByRecurrence(com.example.rewire.core.RecurrenceType.Daily)
    assertEquals(1, daily.size)
    assertEquals("Daily", daily[0].name)
    }

    @Test
    fun testGetHabitsByDate() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(8L, "ByDate", com.example.rewire.core.RecurrenceType.Daily, "15:00", 45, "2025-08-20"))
    val byDate = repo.getHabitsByDate("2025-08-20")
    assertEquals(1, byDate.size)
    assertEquals("ByDate", byDate[0].name)
    }

    @Test
    fun testInsertHabits() = runBlocking {
        habitList.clear()
        val habits = listOf(
            HabitEntity(9L, "Bulk1", com.example.rewire.core.RecurrenceType.Daily, "16:00", 50, "2025-08-21"),
            HabitEntity(10L, "Bulk2", com.example.rewire.core.RecurrenceType.Daily, "17:00", 55, "2025-08-22")
        )
        repo.insertHabits(habits)
        assertTrue(habitList.any { it.name == "Bulk1" })
        assertTrue(habitList.any { it.name == "Bulk2" })
    }

    @Test
    fun testDeleteHabits() = runBlocking {
        habitList.clear()
        val habits = listOf(
            HabitEntity(11L, "Del1", com.example.rewire.core.RecurrenceType.Daily, "18:00", 60, "2025-08-23"),
            HabitEntity(12L, "Del2", com.example.rewire.core.RecurrenceType.Daily, "19:00", 65, "2025-08-24")
        )
        habitList.addAll(habits)
        repo.deleteHabits(habits)
        assertFalse(habitList.any { it.name == "Del1" })
        assertFalse(habitList.any { it.name == "Del2" })
    }

    @Test
    fun testSearchHabits() = runBlocking {
        habitList.clear()
    habitList.add(HabitEntity(13L, "SearchMe", com.example.rewire.core.RecurrenceType.Daily, "20:00", 70, "2025-08-25"))
    val found = repo.searchHabits("search")
    assertEquals(1, found.size)
    assertEquals("SearchMe", found[0].name)
    }

    @Test
    fun testGetCompletedHabits() = runBlocking {
        habitList.clear()
        completionList.clear()
        habitList.add(HabitEntity(14, "Completed", com.example.rewire.core.RecurrenceType.Daily, "21:00", 75, "2025-08-26"))
        completionList.add(com.example.rewire.db.entity.HabitCompletion(1, 14, "2025-08-26"))
        val completed = repo.getCompletedHabits("2025-08-26")
        assertEquals(1, completed.size)
        assertEquals("Completed", completed[0].name)
    }

    @Test
    fun testGetHabitStreak() = runBlocking {
        completionList.clear()
        completionList.add(com.example.rewire.db.entity.HabitCompletion(2, 15, "2025-08-19"))
        completionList.add(com.example.rewire.db.entity.HabitCompletion(3, 15, "2025-08-18"))
        val streak = repo.getHabitStreak(15)
        assertEquals(0, streak) // streak logic is based on consecutive days up to today, adjust as needed
    }

    @Test
    fun testGetHabitsPaged() = runBlocking {
        habitList.clear()
        for (i in 1..10) habitList.add(HabitEntity(i.toLong(), "Paged$i", com.example.rewire.core.RecurrenceType.Daily, "22:00", 80, "2025-08-27"))
        val paged = repo.getHabitsPaged(2, 3)
        assertEquals(3, paged.size)
        assertEquals("Paged3", paged[0].name)
        assertEquals("Paged4", paged[1].name)
        assertEquals("Paged5", paged[2].name)
    }
}
