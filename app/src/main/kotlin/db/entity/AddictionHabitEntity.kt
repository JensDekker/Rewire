package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rewire.core.AddictionHabit
import com.example.rewire.core.RecurrenceType
import java.time.LocalDate
import java.time.LocalTime
import com.example.rewire.db.converter.RecurrenceTypeConverter

@Entity(tableName = "addiction_habits")
@TypeConverters(RecurrenceTypeConverter::class)
data class AddictionHabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: String,
    val recurrence: RecurrenceType,
    val preferredTime: String?,
    val estimatedMinutes: Int?
)

fun AddictionHabitEntity.toCore(): AddictionHabit = AddictionHabit(
    id = id,
    name = name,
    startDate = LocalDate.parse(startDate),
    recurrence = recurrence,
    preferredTime = preferredTime?.let { LocalTime.parse(it) },
    estimatedMinutes = estimatedMinutes
)

fun AddictionHabit.toEntity(): AddictionHabitEntity = AddictionHabitEntity(
    id = id,
    name = name,
    startDate = startDate.toString(),
    recurrence = recurrence,
    preferredTime = preferredTime?.toString(),
    estimatedMinutes = estimatedMinutes
)
