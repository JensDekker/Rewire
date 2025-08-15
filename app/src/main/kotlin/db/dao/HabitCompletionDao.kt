package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.HabitCompletionEntity

@Dao
interface HabitCompletionDao {
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: Long, date: String)

    @Delete
    suspend fun delete(completion: HabitCompletionEntity)
}
