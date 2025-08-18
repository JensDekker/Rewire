package com.example.rewire.app.src.main.kotlin.db.dao

import com.example.rewire.app.src.main.kotlin.db.entity.HabitCompletion

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HabitCompletionDao {
    @Insert
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND date = :date")
    suspend fun deleteCompletion(habitId: Long, date: String)

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId")
    suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletion>
}
