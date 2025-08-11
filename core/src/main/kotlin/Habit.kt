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
        return when (recurrence) {
            RecurrenceType.DAILY -> true
            RecurrenceType.WEEKLY -> date.dayOfWeek == DayOfWeek.MONDAY
            RecurrenceType.MONTHLY -> date.dayOfMonth == 1
            RecurrenceType.QUARTERLY -> date.dayOfMonth == 1 && date.month.value % 3 == 1
            RecurrenceType.WEEKENDS -> date.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            RecurrenceType.WEEKDAYS -> date.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            RecurrenceType.CUSTOM_WEEKLY -> customDays?.contains(date.dayOfWeek) == true
        }
    }

    fun markComplete(date: LocalDate = LocalDate.now()) {
        completions.add(date)
    }

    fun isComplete(date: LocalDate = LocalDate.now()): Boolean = completions.contains(date)

    // Note management is now handled by HabitManager using the shared Note class
}
