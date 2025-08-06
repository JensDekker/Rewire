package com.example.rewire.cli

import java.time.LocalTime
import com.example.rewire.core.HabitManager
import com.example.rewire.core.Habit
import com.example.rewire.core.RecurrenceType

// =====================
// Habit Functions (Completion + CRUD)
// =====================
fun cliDeleteHabit(manager: HabitManager, habitName: String) {
    manager.deleteHabit(habitName)
    println("🗑️ Habit '$habitName' deleted.")
}

fun cliAddHabit(manager: HabitManager) {
    print("Enter habit name: ")
    val name = readLine()?.takeIf { it.isNotBlank() }
    if (name == null) {
        printError("No habit has been created. Habit name cannot be blank.")
        return
    }
    println("Select recurrence type:")
    RecurrenceType.values().forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("Recurrence type (1-${RecurrenceType.values().size}): ")
    val recInput = readLine()?.toIntOrNull()
    val recurrence = recInput?.let { RecurrenceType.values().getOrNull(it - 1) } ?: RecurrenceType.DAILY
    var customDays: Set<java.time.DayOfWeek>? = null
    if (recurrence == RecurrenceType.CUSTOM_WEEKLY) {
        customDays = promptCustomDays()
    }
    print("Start date (YYYY-MM-DD, leave blank for today): ")
    val startDateInput = readLine()?.takeIf { it.isNotBlank() }
    val startDate = try { startDateInput?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now() } catch (e: Exception) { java.time.LocalDate.now() }
    print("Preferred time (HH:mm): ")
    val timeInput = readLine()?.takeIf { it.isNotBlank() }
    val preferredTime = try { timeInput?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0) } catch (e: Exception) { LocalTime.of(9, 0) }
    print("Estimated time in minutes: ")
    val minsInput = readLine()?.toIntOrNull() ?: 0
    manager.addHabit(Habit(name, recurrence, preferredTime, minsInput, customDays = customDays, startDate = startDate))
    println("✅ Habit '$name' added.")
}

fun cliListHabits(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits found.")
        return
    }
    println("Habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
}

fun cliShowHabitDetails(manager: HabitManager, habitName: String) {
    val habit = manager.getHabit(habitName) ?: return
    println("Name: ${habit.name}")
    println("Recurrence: ${habit.recurrence}")
    println("Start date: ${habit.startDate}")
    if (habit.recurrence == RecurrenceType.CUSTOM_WEEKLY) {
        println("Custom days: ${formatCustomDays(habit.customDays)}")
    }
    println("Preferred time: ${habit.preferredTime}")
    println("Estimated minutes: ${habit.estimatedMinutes}")
    println("Notes: ${habit.notes}")
    println("Completions: ${habit.completions}")
}

fun cliEditHabit(manager: HabitManager, habitName: String): String? {
    val oldName = habitName
    val habit = manager.getHabit(oldName) ?: return null

    println("Editing habit: $oldName")
    print("New name (leave blank to keep '${habit.name}'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }

    println("Select new recurrence type or leave blank:")
    RecurrenceType.values().forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("New recurrence type (1-${RecurrenceType.values().size}): ")
    val recInput = readLine()?.toIntOrNull()
    val newRecurrence = recInput?.let { RecurrenceType.values().getOrNull(it - 1) }
    var newCustomDays: Set<java.time.DayOfWeek>? = habit.customDays
    if (newRecurrence == RecurrenceType.CUSTOM_WEEKLY) {
        newCustomDays = promptCustomDays()
    }

    print("New start date (YYYY-MM-DD) or leave blank to keep '${habit.startDate}'): ")
    val startDateInput = readLine()?.takeIf { it.isNotBlank() }
    val newStartDate = try { startDateInput?.let { java.time.LocalDate.parse(it) } ?: habit.startDate } catch (e: Exception) { habit.startDate }

    print("New preferred time (HH:mm) or leave blank to keep '${habit.preferredTime}'): ")
    val timeInput = readLine()?.takeIf { it.isNotBlank() }
    val newPreferredTime = try {
        timeInput?.let { LocalTime.parse(it) } ?: habit.preferredTime
    } catch (e: Exception) {
        habit.preferredTime
    }

    print("New estimated time in minutes or leave blank to keep '${habit.estimatedMinutes}'): ")
    val minsInput = readLine()?.toIntOrNull()
    val newEstimatedMinutes = minsInput ?: habit.estimatedMinutes

    manager.updateHabit(
        name = oldName,
        newName = newName,
        newRecurrence = newRecurrence,
        newPreferredTime = newPreferredTime,
        newEstimatedMinutes = newEstimatedMinutes,
        newCustomDays = newCustomDays
    )
    // Directly update startDate if changed
    val updatedHabit = manager.getHabit(newName ?: oldName)
    if (updatedHabit != null) {
        updatedHabit.startDate = newStartDate
    }
    println("✅ Habit updated.")
    return newName ?: oldName
}

fun cliCompleteHabit(manager: HabitManager, habitName: String) {
    val habit = manager.getHabit(habitName)
    if (habit == null) {
        println("Habit not found.")
        return
    }
    val today = java.time.LocalDate.now()
    if (habit.isComplete(today)) {
        println("ℹ️ Habit '$habitName' was already marked as completed for today.")
    } else {
        manager.markHabitComplete(habitName, today)
        println("✅ Habit '$habitName' marked as completed.")
    }
}

// =====================
// Habit Note Functions (CRUD)
// =====================

fun cliAddHabitNote(manager: HabitManager, habitName: String) {
    print("Enter date for note (YYYY-MM-DD, leave blank for today): ")
    val dateStr = readLine()
    val date = if (dateStr.isNullOrBlank()) java.time.LocalDate.now() else try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    if (manager.getNotes(habitName).containsKey(date)) {
        println("A note already exists for $date. Adding a new note will overwrite the previous one.")
        print("Do you want to proceed? (y/n): ")
        val confirm = readLine()?.trim()?.lowercase()
        if (confirm != "y") {
            println("Note not added.")
            return
        }
    }
    print("Enter note: ")
    val note = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addNote(habitName, date, note)
    println("📝 Note added to '$habitName'.")
}

fun cliListHabitNotes(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes for '$habitName':")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<java.time.LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
}

fun cliDeleteHabitNote(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes:")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<java.time.LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    val date = notes.keys.toList()[noteIndex - 1]
    manager.deleteNote(habitName, date)
    println("🗑️ Note deleted.")
}

fun cliEditHabitNote(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes:")
    notes.entries.forEachIndexed { i, entry -> println("${i + 1}. ${entry.key}: ${entry.value}") }
    print("Select note to edit (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size }
    if (noteIndex == null) {
        println("Invalid selection.")
        return
    }
    val date = notes.keys.toList()[noteIndex - 1]
    print("Enter new note: ")
    val newNote = readLine()?.takeIf { it.isNotBlank() }
    if (newNote == null) {
        println("Note cannot be blank.")
        return
    }
    manager.editNote(habitName, date, newNote)
    println("✏️ Note updated.")
}

fun getHabitNoteDays(manager: HabitManager, habitName: String): List<java.time.LocalDate> {
    return manager.getNotes(habitName).keys.sorted()
}

// CLI functions for habits and habit notes
// Move all habit-related CLI functions here from CliFunctions.kt

// Example stub
// fun cliAddHabit(manager: HabitManager) { /* ... */ }
// fun cliListHabits(manager: HabitManager) { /* ... */ }
// fun cliShowHabitDetails(manager: HabitManager) { /* ... */ }
// fun cliAddHabitNote(manager: HabitManager) { /* ... */ }
// fun cliListHabitNotes(manager: HabitManager) { /* ... */ }
// fun cliEditHabitNote(manager: HabitManager) { /* ... */ }
// fun cliDeleteHabitNote(manager: HabitManager) { /* ... */ }
