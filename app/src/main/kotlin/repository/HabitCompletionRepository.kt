package com.example.rewire.repository

import com.example.rewire.db.dao.HabitCompletionDao
import com.example.rewire.db.entity.HabitCompletion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitCompletionRepository(private val habitCompletionDao: HabitCompletionDao) {
    suspend fun insertCompletion(completion: HabitCompletion) = withContext(Dispatchers.IO) {
        habitCompletionDao.insertCompletion(completion)
    }

    suspend fun deleteCompletion(habitId: Long, date: String) = withContext(Dispatchers.IO) {
        habitCompletionDao.deleteCompletion(habitId, date)
    }

    suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletion> = withContext(Dispatchers.IO) {
        habitCompletionDao.getCompletionsForHabit(habitId)
    }
}
