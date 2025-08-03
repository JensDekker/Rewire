package com.example.rewire.core

import java.time.*

class HabitManager {
    private val habits = mutableListOf<Habit>()

    fun addHabit(habit: Habit) = habits.add(habit)
    fun deleteHabit(name: String) = habits.removeIf { it.name == name }
    fun getHabit(name: String): Habit? = habits.find { it.name == name }
    fun listHabits(): List<String> = habits.map { it.name }

    fun updateHabit(
        name: String,
        newName: String?,
        newRecurrence: RecurrenceType?,
        newPreferredTime: LocalTime,
        newEstimatedMinutes: Int?
    ) {
        getHabit(name)?.let { old ->
            val updated = old.copy(
                name = newName ?: old.name,
                recurrence = newRecurrence ?: old.recurrence,
                preferredTime = newPreferredTime,
                estimatedMinutes = newEstimatedMinutes ?: old.estimatedMinutes
            )
            habits.remove(old)
            habits.add(updated)
        }
    }

    fun getHabitsForDate(date: LocalDate): List<Habit> = habits.filter { it.isDueOn(date) }.sortedBy { it.preferredTime }
    fun markHabitComplete(name: String, date: LocalDate = LocalDate.now()) = getHabit(name)?.markComplete(date)
    fun addNote(name: String, date: LocalDate, note: String) = getHabit(name)?.addNote(date, note)
    fun editNote(name: String, date: LocalDate, newNote: String) = getHabit(name)?.editNote(date, newNote)
    fun deleteNote(name: String, date: LocalDate) = getHabit(name)?.deleteNote(date)
    fun getCompletions(name: String): Set<LocalDate> = getHabit(name)?.completions ?: emptySet()
    fun getNotes(name: String): Map<LocalDate, String> = getHabit(name)?.notes ?: emptyMap()
}
