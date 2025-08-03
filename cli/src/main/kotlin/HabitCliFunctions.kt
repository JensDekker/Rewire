package com.example.rewire.cli

import java.time.LocalTime
import com.example.rewire.core.HabitManager
import com.example.rewire.core.Habit
import com.example.rewire.core.RecurrenceType

// =====================
// Habit Functions (Completion + CRUD)
// =====================
fun cliCompleteHabit(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to complete.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit to complete (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val name = habits[index - 1]
    manager.markHabitComplete(name)
    println("✅ Habit '$name' marked as completed.")
}

fun cliAddHabit(manager: HabitManager) {
    print("Enter habit name: ")
    val name = readLine()?.takeIf { it.isNotBlank() } ?: return
    println("Select recurrence type:")
    RecurrenceType.values().forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("Recurrence type (1-${RecurrenceType.values().size}): ")
    val recInput = readLine()?.toIntOrNull()
    val recurrence = recInput?.let { RecurrenceType.values().getOrNull(it - 1) } ?: RecurrenceType.DAILY
    print("Preferred time (HH:mm): ")
    val timeInput = readLine()?.takeIf { it.isNotBlank() }
    val preferredTime = try { timeInput?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0) } catch (e: Exception) { LocalTime.of(9, 0) }
    print("Estimated time in minutes: ")
    val minsInput = readLine()?.toIntOrNull() ?: 0
    manager.addHabit(Habit(name, recurrence, preferredTime, minsInput))
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

fun cliShowHabitDetails(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to show.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit to view details (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val habit = manager.getHabit(habits[index - 1]) ?: return
    println("Name: ${habit.name}")
    println("Recurrence: ${habit.recurrence}")
    println("Preferred time: ${habit.preferredTime}")
    println("Estimated minutes: ${habit.estimatedMinutes}")
    println("Notes: ${habit.notes}")
    println("Completions: ${habit.completions}")
}

fun cliEditHabit(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to edit.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit to edit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val oldName = habits[index - 1]

    val habit = manager.getHabit(oldName) ?: return

    println("Editing habit: $oldName")
    print("New name (leave blank to keep '${habit.name}'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }

    println("Select new recurrence type or leave blank:")
    RecurrenceType.values().forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("New recurrence type (1-${RecurrenceType.values().size}): ")
    val recInput = readLine()?.toIntOrNull()
    val newRecurrence = recInput?.let { RecurrenceType.values().getOrNull(it - 1) }

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
        newEstimatedMinutes = newEstimatedMinutes
    )
    println("✅ Habit updated.")
}

fun cliDeleteHabit(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to delete.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit to delete (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val nameToDelete = habits[index - 1]
    manager.deleteHabit(nameToDelete)
    println("🗑️ Habit '$nameToDelete' deleted.")
}

// =====================
// Habit Note Functions (CRUD)
// =====================

fun cliAddHabitNote(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to add notes to.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val name = habits[index - 1]
    print("Enter date for note (YYYY-MM-DD): ")
    val dateStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val date = try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    print("Enter note: ")
    val note = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addNote(name, date, note)
    println("📝 Note added to '$name'.")
}

fun cliListHabitNotes(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to list notes for.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val name = habits[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes for '$name':")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<java.time.LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
}

fun cliEditHabitNote(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to edit notes for.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val name = habits[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes:")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<java.time.LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
    print("Select note to edit (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    val date = notes.keys.toList()[noteIndex - 1]
    print("Enter new note: ")
    val newNote = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.editNote(name, date, newNote)
    println("✏️ Note updated.")
}

fun cliDeleteHabitNote(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        println("No habits to delete notes from.")
        return
    }
    println("Available habits:")
    habits.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select habit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..habits.size } ?: return
    val name = habits[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes:")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<java.time.LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    val date = notes.keys.toList()[noteIndex - 1]
    manager.deleteNote(name, date)
    println("🗑️ Note deleted.")
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
