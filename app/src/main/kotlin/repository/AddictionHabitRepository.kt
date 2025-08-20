package com.example.rewire.repository

import com.example.rewire.db.dao.AddictionHabitDao
import com.example.rewire.db.entity.AddictionHabitEntity

class AddictionHabitRepository(private val dao: AddictionHabitDao) {
    suspend fun getAll(): List<AddictionHabitEntity> = dao.getAll()
    suspend fun getById(id: Long): AddictionHabitEntity? = dao.getById(id)
    suspend fun insert(addictionHabit: AddictionHabitEntity): Long = dao.insert(addictionHabit)
    suspend fun update(addictionHabit: AddictionHabitEntity) = dao.update(addictionHabit)
    suspend fun delete(addictionHabit: AddictionHabitEntity) = dao.delete(addictionHabit)
    suspend fun deleteAll() = dao.deleteAll()
}
