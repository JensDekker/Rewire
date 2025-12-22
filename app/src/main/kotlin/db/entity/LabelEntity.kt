package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.rewire.core.Label

@Entity(
    tableName = "labels",
    indices = [Index(value = ["name"], unique = true)]
)
data class LabelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val color: String,
    val createdAt: String? = null
)

fun LabelEntity.toCore(): Label = Label(
    id = id,
    name = name,
    color = color
)

fun Label.toEntity(): LabelEntity = LabelEntity(
    id = id,
    name = name,
    color = color,
    createdAt = null  // Label doesn't have createdAt, so set to null when converting
)

