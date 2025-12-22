package com.example.rewire.manager

import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.repository.LabelRepository
import com.example.rewire.repository.LabelResult
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.db.entity.HabitCompletion
import com.example.rewire.db.entity.HabitNoteEntity
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.toCore

class HabitManager(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val habitNoteRepository: HabitNoteRepository,
    private val labelRepository: LabelRepository
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

    suspend fun updateHabit(habit: HabitEntity) {
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        habitRepository.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitRepository.deleteHabit(habit)
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

    suspend fun insertNote(note: HabitNoteEntity) {
        habitNoteRepository.insertNote(note)
    }

    suspend fun editNote(note: HabitNoteEntity) {
        habitNoteRepository.editNote(note)
    }

    suspend fun deleteNote(note: HabitNoteEntity) {
        habitNoteRepository.deleteNote(note)
    }

    suspend fun getNotesForHabit(habitId: Long): List<HabitNoteEntity> {
        return habitNoteRepository.getNotesForHabit(habitId)
    }

    suspend fun getHabits(): List<HabitEntity> {
        return habitRepository.getAllHabits()
    }

    suspend fun getNoteForHabitOnDate(habitId: Long, date: String): String {
        return habitNoteRepository.getNoteForHabitOnDate(habitId, date)
    }

    // Label-related methods
    
    // Get habit with labels
    suspend fun getHabitWithLabels(habitId: Long): com.example.rewire.core.Habit? {
        val entity = habitRepository.getHabitById(habitId) ?: return null
        return habitRepository.habitEntityToHabit(entity)
    }
    
    // Set labels for a habit
    suspend fun setLabelsForHabit(habitId: Long, labelIds: List<Long>) {
        labelRepository.setLabelsForHabit(habitId, labelIds)
    }
    
    // Get all available labels
    suspend fun getAllLabels(): List<LabelEntity> {
        return labelRepository.getAllLabels()
    }
    
    // Get labels for a habit
    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity> {
        return labelRepository.getLabelsForHabit(habitId)
    }
    
    // Batch get labels for multiple habits (optimization)
    suspend fun getLabelsForHabits(habitIds: List<Long>): Map<Long, List<LabelEntity>> {
        return labelRepository.getLabelsForHabits(habitIds)
    }
    
    // Create habit with labels atomically
    suspend fun createHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Long> {
        return try {
            // Insert habit and get generated ID
            val habitId = habitRepository.insertHabit(habit)
            // Set labels for the new habit
            labelRepository.setLabelsForHabit(habitId, labelIds)
            Result.success(habitId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Update habit with labels atomically
    suspend fun updateHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Unit> {
        return try {
            require(habit.id > 0) { "Habit ID must be greater than 0 for update" }
            // Update habit
            habitRepository.updateHabit(habit)
            // Update labels
            labelRepository.setLabelsForHabit(habit.id, labelIds)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Get habit IDs that use a label (for statistics/usage counts)
    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long> {
        return labelRepository.getHabitIdsWithLabel(labelId)
    }
    
    // Create label with validation
    suspend fun createLabel(label: LabelEntity): LabelResult {
        return labelRepository.insertLabelWithValidation(label)
    }
    
    // Update label with validation
    suspend fun updateLabel(label: LabelEntity): LabelResult {
        return labelRepository.updateLabelWithValidation(label)
    }
    
    // Delete label
    suspend fun deleteLabel(label: LabelEntity) {
        labelRepository.deleteLabel(label)
    }
}
