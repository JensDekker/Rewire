package com.example.rewire.repository

import com.example.rewire.db.dao.HabitDao
import com.example.rewire.db.entity.HabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitRepository(private val habitDao: HabitDao) {
    suspend fun getAllHabits(): List<HabitEntity> = withContext(Dispatchers.IO) {
        habitDao.getAll()
    }

    suspend fun getHabitById(id: Long): HabitEntity? = withContext(Dispatchers.IO) {
        habitDao.getById(id)
    }

    suspend fun insertHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.insert(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.update(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) = withContext(Dispatchers.IO) {
        habitDao.delete(habit)
    }

        // Query by recurrence type
        suspend fun getHabitsByRecurrence(recurrence: com.example.rewire.core.RecurrenceType): List<HabitEntity> = withContext(Dispatchers.IO) {
            habitDao.getAll().filter { it.recurrence == recurrence }
        }

        // Query by date (assuming HabitEntity has a date field)
        suspend fun getHabitsByDate(date: String): List<HabitEntity> = withContext(Dispatchers.IO) {
            habitDao.getAll().filter { it.startDate == date }
        }

        // Bulk insert
        suspend fun insertHabits(habits: List<HabitEntity>) = withContext(Dispatchers.IO) {
            habits.forEach { habitDao.insert(it) }
        }

        // Bulk delete
        suspend fun deleteHabits(habits: List<HabitEntity>) = withContext(Dispatchers.IO) {
            habits.forEach { habitDao.delete(it) }
        }

        // Search habits by name
        suspend fun searchHabits(query: String): List<HabitEntity> = withContext(Dispatchers.IO) {
            habitDao.getAll().filter { it.name.contains(query, ignoreCase = true) }
        }

    // Add HabitCompletionDao as a dependency
    private lateinit var habitCompletionDao: com.example.rewire.db.dao.HabitCompletionDao

    fun setHabitCompletionDao(dao: com.example.rewire.db.dao.HabitCompletionDao) {
        habitCompletionDao = dao
    }

    // Get completed habits for a given date
    suspend fun getCompletedHabits(date: String): List<HabitEntity> = withContext(Dispatchers.IO) {
        // Get all completions for all habits on the given date
        val allHabits = habitDao.getAll()
        val completedHabitIds = allHabits.mapNotNull { habit ->
            val completions = habitCompletionDao.getCompletionsForHabit(habit.id)
            completions.find { it.date == date }?.habitId
        }
        allHabits.filter { it.id in completedHabitIds }
    }

    // Get habit streak: count consecutive days with completions up to today
    suspend fun getHabitStreak(habitId: Long): Int = withContext(Dispatchers.IO) {
        val completions = habitCompletionDao.getCompletionsForHabit(habitId)
        val dates = completions.map { it.date }.sortedDescending()
        if (dates.isEmpty()) return@withContext 0
        var streak = 0
        var currentDate = java.time.LocalDate.now().toString()
        for (date in dates) {
            if (date == currentDate) {
                streak++
                currentDate = java.time.LocalDate.parse(currentDate).minusDays(1).toString()
            } else {
                break
            }
        }
        streak
    }

        // Paging
        suspend fun getHabitsPaged(offset: Int, limit: Int): List<HabitEntity> = withContext(Dispatchers.IO) {
            habitDao.getAll().drop(offset).take(limit)
        }
}
