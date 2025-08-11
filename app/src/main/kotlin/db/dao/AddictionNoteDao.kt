package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.AddictionNoteEntity

@Dao
interface AddictionNoteDao {
    @Query("SELECT * FROM addiction_notes")
    suspend fun getAll(): List<AddictionNoteEntity>

    @Query("SELECT * FROM addiction_notes WHERE id = :id")
    suspend fun getById(id: Long): AddictionNoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: AddictionNoteEntity): Long

    @Update
    suspend fun update(note: AddictionNoteEntity)

    @Delete
    suspend fun delete(note: AddictionNoteEntity)

    @Query("DELETE FROM addiction_notes")
    suspend fun deleteAll()
}
