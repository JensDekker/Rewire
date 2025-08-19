package com.example.rewire.repository

import com.example.rewire.db.dao.HabitNoteDao
import com.example.rewire.db.entity.HabitNoteEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitNoteRepository(private val habitNoteDao: HabitNoteDao) {
    suspend fun editNote(note: HabitNoteEntity) = withContext(Dispatchers.IO) {
        habitNoteDao.update(note)
    }
    suspend fun insertNote(note: HabitNoteEntity) = withContext(Dispatchers.IO) {
        habitNoteDao.insert(note)
    }

    suspend fun deleteNote(note: HabitNoteEntity) = withContext(Dispatchers.IO) {
        habitNoteDao.delete(note)
    }

    suspend fun getNotesForHabit(habitId: Long): List<HabitNoteEntity> = withContext(Dispatchers.IO) {
        habitNoteDao.getAll().filter { it.habitId == habitId }
    }
}
