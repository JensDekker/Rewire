package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.AddictionHabitEntity

@Dao
interface AddictionHabitDao {
    @Query("SELECT * FROM addiction_habits")
    suspend fun getAll(): List<AddictionHabitEntity>

    @Query("SELECT * FROM addiction_habits WHERE id = :id")
    suspend fun getById(id: Long): AddictionHabitEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(addictionHabit: AddictionHabitEntity): Long

    @Update
    suspend fun update(addictionHabit: AddictionHabitEntity)

    @Delete
    suspend fun delete(addictionHabit: AddictionHabitEntity)

    @Query("DELETE FROM addiction_habits")
    suspend fun deleteAll()
}
