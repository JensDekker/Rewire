package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.TypeConverters
import com.example.rewire.core.AbstinenceGoal
import com.example.rewire.core.RecurrenceType
import com.example.rewire.db.converter.RecurrenceTypeConverter

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
@TypeConverters(RecurrenceTypeConverter::class)
data class AbstinenceGoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val addictionId: Long,
    val recurrence: RecurrenceType,
    val value: Int,
    val repeatCount: Int
)

fun AbstinenceGoalEntity.toCore(): AbstinenceGoal = AbstinenceGoal(
    id = id,
    addictionId = addictionId,
    recurrence = recurrence,
    value = value,
    repeatCount = repeatCount
)

fun AbstinenceGoal.toEntity(): AbstinenceGoalEntity = AbstinenceGoalEntity(
    id = id,
    addictionId = addictionId,
    recurrence = recurrence,
    value = value,
    repeatCount = repeatCount
)
