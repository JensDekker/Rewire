package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "habit_notes",
    foreignKeys = [
        ForeignKey(
            entity = com.example.rewire.db.entity.HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["habitId"])]
)
data class HabitNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val habitId: Long,
    val content: String,
    val timestamp: String
)
