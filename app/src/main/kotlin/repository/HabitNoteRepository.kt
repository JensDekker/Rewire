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

    /**
     * Insert or update today's (or dated) note for a habit.
     *
     * Room `@Insert(REPLACE)` only conflicts on primary key. Saving with `id = 0`
     * always creates a new row, so reloads with LIMIT 1 can keep returning the
     * original note. Upsert by habitId + date instead.
     */
    suspend fun upsertNoteForDate(habitId: Long, content: String, date: String) =
        withContext(Dispatchers.IO) {
            val existing = habitNoteDao.getNoteForHabitOnDate(habitId, date)
            if (existing != null) {
                habitNoteDao.update(existing.copy(content = content))
            } else {
                habitNoteDao.insert(
                    HabitNoteEntity(
                        habitId = habitId,
                        content = content,
                        timestamp = date
                    )
                )
            }
        }

    suspend fun deleteNote(note: HabitNoteEntity) = withContext(Dispatchers.IO) {
        habitNoteDao.delete(note)
    }

    suspend fun getNotesForHabit(habitId: Long): List<HabitNoteEntity> = withContext(Dispatchers.IO) {
        habitNoteDao.getAll().filter { it.habitId == habitId }
    }

    suspend fun getNoteForHabitOnDate(habitId: Long, date: String): String = withContext(Dispatchers.IO) {
        habitNoteDao.getNoteForHabitOnDate(habitId, date)?.content ?: ""
    }
}
