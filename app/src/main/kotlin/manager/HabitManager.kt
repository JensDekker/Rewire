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

class HabitManager(
    private val habitRepository: HabitRepository,
    private val habitCompletionRepository: HabitCompletionRepository,
    private val habitNoteRepository: HabitNoteRepository,
    private val labelRepository: LabelRepository,
    val notificationScheduler: HabitNotificationScheduler? = null
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
                is com.example.rewire.core.RecurrenceType.Weekly -> {
                    // Once per week on the same weekday as startDate
                    val startDayOfWeek = com.example.rewire.core.DayOfWeek.valueOf(start.dayOfWeek.name)
                    dayOfWeek == startDayOfWeek
                }
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
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        habitRepository.insertHabit(habit)
        notificationScheduler?.scheduleNotificationsForHabit(habit, this)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        require(habit.name.isNotBlank()) { "Habit name cannot be blank" }
        habitRepository.updateHabit(habit)
        notificationScheduler?.cancelNotificationsForHabit(habit.id)
        notificationScheduler?.scheduleNotificationsForHabit(habit, this)
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitRepository.deleteHabit(habit)
        notificationScheduler?.cancelNotificationsForHabit(habit.id)
    }

    suspend fun completeHabit(habitId: Long, date: String = java.time.LocalDate.now().toString()) {
        val completion = HabitCompletion(habitId = habitId, date = date)
        habitCompletionRepository.insertCompletion(completion)
    }

    suspend fun isHabitCompletedForDate(habitId: Long, date: String): Boolean {
        return habitCompletionRepository.isHabitCompletedForDate(habitId, date)
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

    /** Persist today's (or dated) note content, updating in place when a row already exists. */
    suspend fun upsertNoteForDate(habitId: Long, content: String, date: String) {
        habitNoteRepository.upsertNoteForDate(habitId, content, date)
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

    suspend fun getHabitWithLabels(habitId: Long): com.example.rewire.core.Habit? {
        val entity = habitRepository.getHabitById(habitId) ?: return null
        return habitRepository.habitEntityToHabit(entity)
    }

    suspend fun setLabelsForHabit(habitId: Long, labelIds: List<Long>) {
        labelRepository.setLabelsForHabit(habitId, labelIds)
    }

    suspend fun getAllLabels(): List<LabelEntity> {
        return labelRepository.getAllLabels()
    }

    suspend fun getLabelsForHabit(habitId: Long): List<LabelEntity> {
        return labelRepository.getLabelsForHabit(habitId)
    }

    suspend fun getLabelsForHabits(habitIds: List<Long>): Map<Long, List<LabelEntity>> {
        return labelRepository.getLabelsForHabits(habitIds)
    }

    suspend fun createHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Long> {
        return try {
            val habitId = habitRepository.insertHabit(habit)
            labelRepository.setLabelsForHabit(habitId, labelIds)
            val saved = habit.copy(id = habitId)
            notificationScheduler?.scheduleNotificationsForHabit(saved, this)
            Result.success(habitId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateHabitWithLabels(habit: HabitEntity, labelIds: List<Long>): Result<Unit> {
        return try {
            require(habit.id > 0) { "Habit ID must be greater than 0 for update" }
            habitRepository.updateHabit(habit)
            labelRepository.setLabelsForHabit(habit.id, labelIds)
            notificationScheduler?.cancelNotificationsForHabit(habit.id)
            notificationScheduler?.scheduleNotificationsForHabit(habit, this)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHabitIdsWithLabel(labelId: Long): List<Long> {
        return labelRepository.getHabitIdsWithLabel(labelId)
    }

    suspend fun createLabel(label: LabelEntity): LabelResult {
        return labelRepository.insertLabelWithValidation(label)
    }

    suspend fun updateLabel(label: LabelEntity): LabelResult {
        return labelRepository.updateLabelWithValidation(label)
    }

    suspend fun deleteLabel(label: LabelEntity) {
        labelRepository.deleteLabel(label)
    }
}
