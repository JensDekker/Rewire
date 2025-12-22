package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.HabitLabelCrossRef

@Dao
interface HabitLabelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(crossRef: HabitLabelCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(crossRefs: List<HabitLabelCrossRef>)
    
    @Delete
    suspend fun delete(crossRef: HabitLabelCrossRef)
    
    @Query("DELETE FROM habit_labels WHERE habitId = :habitId")
    suspend fun deleteAllForHabit(habitId: Long)
    
    @Query("DELETE FROM habit_labels WHERE labelId = :labelId")
    suspend fun deleteAllForLabel(labelId: Long)
    
    @Query("SELECT * FROM habit_labels WHERE habitId = :habitId")
    suspend fun getCrossRefsForHabit(habitId: Long): List<HabitLabelCrossRef>
    
    @Query("SELECT * FROM habit_labels WHERE labelId = :labelId")
    suspend fun getCrossRefsForLabel(labelId: Long): List<HabitLabelCrossRef>
    
    // Check if a habit has a specific label
    @Query("SELECT EXISTS(SELECT 1 FROM habit_labels WHERE habitId = :habitId AND labelId = :labelId)")
    suspend fun hasLabel(habitId: Long, labelId: Long): Boolean
    
    // Get all habit IDs with a specific label
    @Query("SELECT habitId FROM habit_labels WHERE labelId = :labelId")
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long>
}

