package com.example.rewire.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.Habit
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val recurrence: String, // Store as String, use TypeConverter for RecurrenceType
    val preferredTime: String,
    val estimatedMinutes: Int,
    val startDate: String
)

fun HabitEntity.toCore(): Habit = Habit(
    id = id,
    name = name,
    recurrence = RecurrenceType.valueOf(recurrence),
    preferredTime = LocalTime.parse(preferredTime),
    estimatedMinutes = estimatedMinutes,
    startDate = LocalDate.parse(startDate)
)

fun Habit.toEntity(): HabitEntity = HabitEntity(
    id = id,
    name = name,
    recurrence = recurrence.name,
    preferredTime = preferredTime.toString(),
    estimatedMinutes = estimatedMinutes,
    startDate = startDate.toString()
)
