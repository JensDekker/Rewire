package com.example.rewire.manager

import android.content.Context
import com.example.rewire.db.RewireDatabase
import com.example.rewire.repository.HabitCompletionRepository
import com.example.rewire.repository.HabitNoteRepository
import com.example.rewire.repository.HabitRepository
import com.example.rewire.repository.LabelRepository

/**
 * Builds [HabitManager] (+ optional notification scheduler) from application context.
 * Used by MainActivity and background receivers.
 */
object HabitManagerFactory {
    fun create(
        context: Context,
        withScheduler: Boolean = true
    ): HabitManager {
        val appContext = context.applicationContext
        val database = RewireDatabase.getInstance(appContext)
        val labelRepository = LabelRepository(database.labelDao(), database.habitLabelDao())
        val habitRepository = HabitRepository(database.habitDao(), labelRepository)
        val habitCompletionRepository = HabitCompletionRepository(database.habitCompletionDao())
        val habitNoteRepository = HabitNoteRepository(database.habitNoteDao())
        val scheduler = if (withScheduler) HabitNotificationScheduler(appContext) else null
        return HabitManager(
            habitRepository = habitRepository,
            habitCompletionRepository = habitCompletionRepository,
            habitNoteRepository = habitNoteRepository,
            labelRepository = labelRepository,
            notificationScheduler = scheduler
        )
    }
}
