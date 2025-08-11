package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.HabitNoteEntity

@Dao
interface HabitNoteDao {
    @Query("SELECT * FROM habit_notes")
    suspend fun getAll(): List<HabitNoteEntity>

    @Query("SELECT * FROM habit_notes WHERE id = :id")
    suspend fun getById(id: Long): HabitNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: HabitNoteEntity): Long

    @Update
    suspend fun update(note: HabitNoteEntity)

    @Delete
    suspend fun delete(note: HabitNoteEntity)

    @Query("DELETE FROM habit_notes")
    suspend fun deleteAll()
}
