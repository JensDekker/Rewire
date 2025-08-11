package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import com.example.rewire.core.AbstinenceGoal
import com.example.rewire.core.RecurrenceType

@Entity(
    tableName = "abstinence_goals",
    foreignKeys = [
        ForeignKey(
            entity = AddictionHabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["addictionId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AbstinenceGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val addictionId: Long,
    val recurrence: String, // Store as String, use TypeConverter for RecurrenceType
    val value: Int,
    val repeatCount: Int
)

fun AbstinenceGoalEntity.toCore(): AbstinenceGoal = AbstinenceGoal(
    id = id,
    addictionId = addictionId,
    recurrence = RecurrenceType.valueOf(recurrence),
    value = value,
    repeatCount = repeatCount
)

fun AbstinenceGoal.toEntity(): AbstinenceGoalEntity = AbstinenceGoalEntity(
    id = id,
    addictionId = addictionId,
    recurrence = recurrence.name,
    value = value,
    repeatCount = repeatCount
)
