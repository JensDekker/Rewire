package com.example.rewire.db.dao

import androidx.room.*
import com.example.rewire.db.entity.LabelEntity

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY name ASC")
    suspend fun getAll(): List<LabelEntity>
    
    @Query("SELECT * FROM labels WHERE id = :id")
    suspend fun getById(id: Long): LabelEntity?
    
    @Query("SELECT * FROM labels WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): LabelEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(label: LabelEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(labels: List<LabelEntity>): List<Long>
    
    @Update
    suspend fun update(label: LabelEntity)
    
    @Delete
    suspend fun delete(label: LabelEntity)
    
    // Get labels for a specific habit (via junction table)
    @Query("""
        SELECT labels.* FROM labels
        INNER JOIN habit_labels ON labels.id = habit_labels.labelId
        WHERE habit_labels.habitId = :habitId
        ORDER BY labels.name ASC
    """)
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity>
    
    // Search labels by name
    @Query("SELECT * FROM labels WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    suspend fun searchLabels(query: String): List<LabelEntity>
}

