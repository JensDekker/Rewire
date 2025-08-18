package com.example.rewire.core

import java.time.*

// --- Habit Class ---
data class Habit(
    var id: Long = 0,
    var name: String,
    var recurrence: RecurrenceType = RecurrenceType.Daily,
    var preferredTime: LocalTime = LocalTime.of(9, 0),
    var estimatedMinutes: Int = 10,
    // completions are now managed by RoomDB
    var customDays: Set<DayOfWeek>? = null,
    var startDate: LocalDate = LocalDate.now()
) {
    private fun toCustomDayOfWeek(javaDayOfWeek: java.time.DayOfWeek): DayOfWeek {
        return when (javaDayOfWeek) {
            java.time.DayOfWeek.MONDAY -> DayOfWeek.MONDAY
            java.time.DayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
            java.time.DayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
            java.time.DayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
            java.time.DayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
            java.time.DayOfWeek.SATURDAY -> DayOfWeek.SATURDAY
            java.time.DayOfWeek.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

    fun isDueOn(date: LocalDate): Boolean {
        if (date.isBefore(startDate)) return false
        val customDayOfWeek = toCustomDayOfWeek(date.dayOfWeek)
        return when (val r = recurrence) {
            is RecurrenceType.Daily -> true
            is RecurrenceType.Weekly -> customDayOfWeek == DayOfWeek.MONDAY
            is RecurrenceType.MonthlyByDate -> date.dayOfMonth == r.dayOfMonth
            is RecurrenceType.MonthlyByWeekday -> {
                val weekOfMonth = (date.dayOfMonth - 1) / 7 + 1
                weekOfMonth == r.weekOfMonth && customDayOfWeek == r.dayOfWeek
            }
            is RecurrenceType.QuarterlyByDate -> {
                val isQuarterMonth = (date.monthValue - r.monthOffset) % 3 == 1
                isQuarterMonth && date.dayOfMonth == r.dayOfMonth
            }
            is RecurrenceType.QuarterlyByWeekday -> {
                val isQuarterMonth = (date.monthValue - r.monthOffset) % 3 == 1
                val weekOfMonth = (date.dayOfMonth - 1) / 7 + 1
                isQuarterMonth && weekOfMonth == r.weekOfMonth && customDayOfWeek == r.dayOfWeek
            }
            is RecurrenceType.CustomWeekly -> r.daysOfWeek.contains(customDayOfWeek)
            // For legacy types, fallback to default behaviors
            else -> false
        }
    }

    // Completion logic is now handled by HabitManager and RoomDB

    // Note management is now handled by HabitManager using the shared Note class
}
