package com.example.rewire.repository

import com.example.rewire.db.dao.HabitNoteDao
import com.example.rewire.db.entity.HabitNote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HabitNoteRepository(private val habitNoteDao: HabitNoteDao) {
    suspend fun editNote(note: com.example.rewire.db.entity.HabitNoteEntity) = withContext(Dispatchers.IO) {
        habitNoteDao.update(note)
    }
    suspend fun insertNote(note: HabitNote) = withContext(Dispatchers.IO) {
        habitNoteDao.insertNote(note)
    }

    suspend fun deleteNote(note: HabitNote) = withContext(Dispatchers.IO) {
        habitNoteDao.deleteNote(note)
    }

    suspend fun getNotesForHabit(habitId: Long): List<HabitNote> = withContext(Dispatchers.IO) {
        habitNoteDao.getNotesForHabit(habitId)
    }
}
