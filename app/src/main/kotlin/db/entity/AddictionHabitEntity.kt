package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rewire.core.AddictionHabit
import com.example.rewire.core.RecurrenceType
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "addiction_habits")
data class AddictionHabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val startDate: String,
    val recurrence: String, // Store as String, use TypeConverter for RecurrenceType
    val preferredTime: String?,
    val estimatedMinutes: Int?
)

fun AddictionHabitEntity.toCore(): AddictionHabit = AddictionHabit(
    id = id,
    name = name,
    startDate = LocalDate.parse(startDate),
    recurrence = RecurrenceType.valueOf(recurrence),
    preferredTime = preferredTime?.let { LocalTime.parse(it) },
    estimatedMinutes = estimatedMinutes
)

fun AddictionHabit.toEntity(): AddictionHabitEntity = AddictionHabitEntity(
    id = id,
    name = name,
    startDate = startDate.toString(),
    recurrence = recurrence.name,
    preferredTime = preferredTime?.toString(),
    estimatedMinutes = estimatedMinutes
)
