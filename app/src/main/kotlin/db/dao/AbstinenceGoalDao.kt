package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.AbstinenceGoalEntity

@Dao
interface AbstinenceGoalDao {
    @Query("SELECT * FROM abstinence_goals")
    suspend fun getAll(): List<AbstinenceGoalEntity>

    @Query("SELECT * FROM abstinence_goals WHERE id = :id")
    suspend fun getById(id: Long): AbstinenceGoalEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(goal: AbstinenceGoalEntity): Long

    @Update
    suspend fun update(goal: AbstinenceGoalEntity)

    @Delete
    suspend fun delete(goal: AbstinenceGoalEntity)

    @Query("DELETE FROM abstinence_goals")
    suspend fun deleteAll()
}
