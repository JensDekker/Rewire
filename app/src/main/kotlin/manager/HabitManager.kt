package com.example.rewire.manager

import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitCompletion
import com.example.rewire.db.entity.HabitNote
import com.example.rewire.db.entity.HabitNoteEntity

class HabitManager(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val habitNoteRepository: HabitNoteRepository
) {
    /**
     * Find all habits due on a given day.
     * Supports all recurrence types defined in RecurrenceType.
     */
    suspend fun getHabitsDueOn(date: String): List<HabitEntity> {
        val allHabits = habitRepository.getAllHabits()
        val targetDate = java.time.LocalDate.parse(date)
        val dayOfMonth = targetDate.dayOfMonth
        val weekOfMonth = (targetDate.dayOfMonth - 1) / 7 + 1
        val dayOfWeek = com.example.rewire.core.DayOfWeek.valueOf(targetDate.dayOfWeek.name)
        val month = targetDate.monthValue
        return allHabits.filter { habit ->
            val start = java.time.LocalDate.parse(habit.startDate)
            if (start.isAfter(targetDate)) return@filter false
            when (val recurrence = habit.recurrence) {
                is com.example.rewire.core.RecurrenceType.Daily -> true
                is com.example.rewire.core.RecurrenceType.Weekly -> true // Every week, same weekday as startDate
                is com.example.rewire.core.RecurrenceType.MonthlyByDate -> dayOfMonth == recurrence.dayOfMonth
                is com.example.rewire.core.RecurrenceType.MonthlyByWeekday -> weekOfMonth == recurrence.weekOfMonth && dayOfWeek == recurrence.dayOfWeek
                is com.example.rewire.core.RecurrenceType.QuarterlyByDate -> {
                    // Due if monthOffset matches and dayOfMonth matches
                    ((month - 1) % 3 == recurrence.monthOffset) && dayOfMonth == recurrence.dayOfMonth
                }
                is com.example.rewire.core.RecurrenceType.QuarterlyByWeekday -> {
                    ((month - 1) % 3 == recurrence.monthOffset) && weekOfMonth == recurrence.weekOfMonth && dayOfWeek == recurrence.dayOfWeek
                }
                is com.example.rewire.core.RecurrenceType.CustomWeekly -> recurrence.daysOfWeek.contains(dayOfWeek)
            }
        }
    }
    suspend fun createHabit(habit: HabitEntity) {
        // Example: validate habit name is not empty
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        // Add more business rules as needed
        habitRepository.insertHabit(habit)
    }

    suspend fun completeHabit(habitId: Long, date: String = java.time.LocalDate.now().toString()) {
        val completion = HabitCompletion(habitId = habitId, date = date)
        habitCompletionRepository.insertCompletion(completion)
    }

    suspend fun deleteCompletion(habitId: Long, date: String) {
        habitCompletionRepository.deleteCompletion(habitId, date)
    }

    suspend fun getCompletionsForHabit(habitId: Long): List<HabitCompletion> {
        return habitCompletionRepository.getCompletionsForHabit(habitId)
    }

    suspend fun insertNote(note: HabitNote) {
        habitNoteRepository.insertNote(note)
    }

    suspend fun editNote(note: HabitNoteEntity) {
        habitNoteRepository.editNote(note)
    }

    suspend fun deleteNote(note: HabitNote) {
        habitNoteRepository.deleteNote(note)
    }

    suspend fun getNotesForHabit(habitId: Long): List<HabitNote> {
        return habitNoteRepository.getNotesForHabit(habitId)
    }

    suspend fun getHabits(): List<HabitEntity> {
        return habitRepository.getAllHabits()
    }
}
