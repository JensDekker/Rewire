package com.example.rewire.manager

import com.example.rewire.db.entity.AbstinenceGoalEntity
import com.example.rewire.db.entity.AddictionHabitEntity
import com.example.rewire.db.entity.AddictionNoteEntity
import com.example.rewire.repository.AbstinenceGoalRepository
import com.example.rewire.repository.AddictionHabitRepository
import com.example.rewire.repository.AddictionNoteRepository

class AddictionManager(
    private val abstinenceGoalRepository: AbstinenceGoalRepository,
    private val addictionHabitRepository: AddictionHabitRepository,
    private val addictionNoteRepository: AddictionNoteRepository
) {
    // AbstinenceGoal functions
    suspend fun getAllAbstinenceGoals(): List<AbstinenceGoalEntity> = abstinenceGoalRepository.getAll()
    suspend fun getAbstinenceGoalById(id: Long): AbstinenceGoalEntity? = abstinenceGoalRepository.getById(id)
    suspend fun addAbstinenceGoal(goal: AbstinenceGoalEntity): Long = abstinenceGoalRepository.insert(goal)
    suspend fun updateAbstinenceGoal(goal: AbstinenceGoalEntity) = abstinenceGoalRepository.update(goal)
    suspend fun deleteAbstinenceGoal(goal: AbstinenceGoalEntity) = abstinenceGoalRepository.delete(goal)
    suspend fun deleteAllAbstinenceGoals() = abstinenceGoalRepository.deleteAll()

    // AddictionHabit functions
    suspend fun getAllAddictionHabits(): List<AddictionHabitEntity> = addictionHabitRepository.getAll()
    suspend fun getAddictionHabitById(id: Long): AddictionHabitEntity? = addictionHabitRepository.getById(id)
    suspend fun addAddictionHabit(habit: AddictionHabitEntity): Long = addictionHabitRepository.insert(habit)
    suspend fun updateAddictionHabit(habit: AddictionHabitEntity) = addictionHabitRepository.update(habit)
    suspend fun deleteAddictionHabit(habit: AddictionHabitEntity) = addictionHabitRepository.delete(habit)
    suspend fun deleteAllAddictionHabits() = addictionHabitRepository.deleteAll()

    // AddictionNote functions
    suspend fun getAllAddictionNotes(): List<AddictionNoteEntity> = addictionNoteRepository.getAll()
    suspend fun getAddictionNoteById(id: Long): AddictionNoteEntity? = addictionNoteRepository.getById(id)
    suspend fun addAddictionNote(note: AddictionNoteEntity): Long = addictionNoteRepository.insert(note)
    suspend fun updateAddictionNote(note: AddictionNoteEntity) = addictionNoteRepository.update(note)
    suspend fun deleteAddictionNote(note: AddictionNoteEntity) = addictionNoteRepository.delete(note)
    suspend fun deleteAllAddictionNotes() = addictionNoteRepository.deleteAll()
}
