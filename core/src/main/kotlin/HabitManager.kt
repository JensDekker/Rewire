package com.example.rewire.core

import java.time.*
import java.time.LocalDate
// ...existing code...

class HabitManager {
    private val habits = mutableListOf<Habit>()
    private val notes = mutableListOf<Note>() // Shared notes for all habits

    fun addHabit(habit: Habit) = habits.add(habit)
    fun deleteHabit(name: String) = habits.removeIf { it.name == name }
    fun getHabit(name: String): Habit? = habits.find { it.name == name }
    fun listHabits(): List<String> = habits.map { it.name }

    fun updateHabit(
        name: String,
        newName: String?,
        newRecurrence: RecurrenceType?,
        newPreferredTime: LocalTime,
        newEstimatedMinutes: Int?,
        newCustomDays: Set<DayOfWeek>? = null
    ) {
        getHabit(name)?.let { old ->
            val updated = old.copy(
                name = newName ?: old.name,
                recurrence = newRecurrence ?: old.recurrence,
                preferredTime = newPreferredTime,
                estimatedMinutes = newEstimatedMinutes ?: old.estimatedMinutes,
                customDays = newCustomDays ?: old.customDays
            )
            habits.remove(old)
            habits.add(updated)
        }
    }

    fun getHabitsForDate(date: LocalDate): List<Habit> = habits.filter { it.isDueOn(date) }.sortedBy { it.preferredTime }
    // All RoomDB access must be handled in the app module. Only pure business logic and core models here.
    fun addNote(name: String, date: LocalDate, note: String) {
        val habit = getHabit(name) ?: return
        notes.removeIf { it.parentType == "HABIT" && it.parentNameOrId == habit.name && it.date == date }
        notes.add(Note(parentType = "HABIT", parentNameOrId = habit.name, date = date, text = note))
    }

    fun editNote(name: String, date: LocalDate, newNote: String) {
        val habit = getHabit(name) ?: return
        notes.find { it.parentType == "HABIT" && it.parentNameOrId == habit.name && it.date == date }?.let {
            notes.remove(it)
            notes.add(it.copy(text = newNote))
        }
    }

    fun deleteNote(name: String, date: LocalDate) {
        val habit = getHabit(name) ?: return
        notes.removeIf { it.parentType == "HABIT" && it.parentNameOrId == habit.name && it.date == date }
    }
    fun getNotes(name: String): List<Note> = notes.filter { it.parentType == "HABIT" && it.parentNameOrId == name }
    fun getNoteDays(name: String): List<LocalDate> = getNotes(name).map { it.date }.sorted()
}
