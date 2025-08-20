package com.example.rewire.repository

import com.example.rewire.db.dao.AbstinenceGoalDao
import com.example.rewire.db.entity.AbstinenceGoalEntity

class AbstinenceGoalRepository(private val dao: AbstinenceGoalDao) {
    suspend fun getAll(): List<AbstinenceGoalEntity> = dao.getAll()
    suspend fun getById(id: Long): AbstinenceGoalEntity? = dao.getById(id)
    suspend fun insert(goal: AbstinenceGoalEntity): Long = dao.insert(goal)
    suspend fun update(goal: AbstinenceGoalEntity) = dao.update(goal)
    suspend fun delete(goal: AbstinenceGoalEntity) = dao.delete(goal)
    suspend fun deleteAll() = dao.deleteAll()
}
