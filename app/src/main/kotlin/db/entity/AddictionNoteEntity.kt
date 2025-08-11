package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey

@Entity(
    tableName = "addiction_notes",
    foreignKeys = [
        ForeignKey(
            entity = com.example.rewire.db.entity.AddictionHabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["addictionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AddictionNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val addictionId: Long,
    val content: String,
    val timestamp: String
)
