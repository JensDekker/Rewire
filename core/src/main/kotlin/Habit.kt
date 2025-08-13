package com.example.rewire.core

import java.time.*

// --- Habit Class ---
data class Habit(
    var id: Long = 0,
    var name: String,
    var recurrence: RecurrenceType = RecurrenceType.DAILY,
    var preferredTime: LocalTime = LocalTime.of(9, 0),
    var estimatedMinutes: Int = 10,
    var completions: MutableSet<LocalDate> = mutableSetOf(),
    var customDays: Set<DayOfWeek>? = null,
    var startDate: LocalDate = LocalDate.now()
) {
    fun isDueOn(date: LocalDate): Boolean {
        if (date.isBefore(startDate)) return false
        return when (val r = recurrence) {
            is RecurrenceType.Daily -> true
            is RecurrenceType.Weekly -> date.dayOfWeek == DayOfWeek.MONDAY
            is RecurrenceType.MonthlyByDate -> date.dayOfMonth == r.dayOfMonth
            is RecurrenceType.MonthlyByWeekday -> {
                val weekOfMonth = (date.dayOfMonth - 1) / 7 + 1
                weekOfMonth == r.weekOfMonth && DayOfWeek.valueOf(date.dayOfWeek.name) == r.dayOfWeek
            }
            is RecurrenceType.QuarterlyByDate -> {
                val isQuarterMonth = (date.monthValue - r.monthOffset) % 3 == 1
                isQuarterMonth && date.dayOfMonth == r.dayOfMonth
            }
            is RecurrenceType.QuarterlyByWeekday -> {
                val isQuarterMonth = (date.monthValue - r.monthOffset) % 3 == 1
                val weekOfMonth = (date.dayOfMonth - 1) / 7 + 1
                isQuarterMonth && weekOfMonth == r.weekOfMonth && DayOfWeek.valueOf(date.dayOfWeek.name) == r.dayOfWeek
            }
            is RecurrenceType.CustomWeekly -> r.daysOfWeek.map { it.name }.contains(date.dayOfWeek.name)
            // For legacy types, fallback to default behaviors
            else -> false
        }
    }

    fun markComplete(date: LocalDate = LocalDate.now()) {
        completions.add(date)
    }

    fun isComplete(date: LocalDate = LocalDate.now()): Boolean = completions.contains(date)

    // Note management is now handled by HabitManager using the shared Note class
}
