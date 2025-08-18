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
    println("Habit '$habitName' deleted.")
}

fun cliAddHabit(manager: HabitManager) {
    print("Enter habit name: ")
    val name = readLine()?.takeIf { it.isNotBlank() }
    if (name == null) {
        printError("No habit has been created. Habit name cannot be blank.")
        return
    }
    val recurrenceOptions = listOf(
        "Daily",
        "Weekly",
        "Monthly by date",
        "Monthly by weekday",
        "Quarterly by date",
        "Quarterly by weekday",
        "Custom weekly"
    )
    println("Select recurrence type:")
    recurrenceOptions.forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("Recurrence type (1-${recurrenceOptions.size}): ")
    val recInput = readLine()?.toIntOrNull()
    val recurrence = when (recInput) {
        1 -> com.example.rewire.core.RecurrenceType.Daily
        2 -> com.example.rewire.core.RecurrenceType.Weekly
        3 -> {
            print("Enter day of month (1-31): ")
            val day = readLine()?.toIntOrNull() ?: 1
            com.example.rewire.core.RecurrenceType.MonthlyByDate(day)
        }
        4 -> {
            print("Enter week of month (1-5): ")
            val week = readLine()?.toIntOrNull() ?: 1
            print("Enter day of week (e.g., MONDAY): ")
            val dayStr = readLine()?.uppercase() ?: "MONDAY"
            val day = com.example.rewire.core.DayOfWeek.valueOf(dayStr)
            com.example.rewire.core.RecurrenceType.MonthlyByWeekday(week, day)
        }
        5 -> {
            print("Enter day of month (1-31): ")
            val day = readLine()?.toIntOrNull() ?: 1
            print("Enter month offset (0 for Jan/Apr/Jul/Oct, 1 for Feb/May/Aug/Nov, 2 for Mar/Jun/Sep/Dec): ")
            val offset = readLine()?.toIntOrNull() ?: 0
            com.example.rewire.core.RecurrenceType.QuarterlyByDate(day, offset)
        }
        6 -> {
            print("Enter week of month (1-5): ")
            val week = readLine()?.toIntOrNull() ?: 1
            print("Enter day of week (e.g., MONDAY): ")
            val dayStr = readLine()?.uppercase() ?: "MONDAY"
            val day = com.example.rewire.core.DayOfWeek.valueOf(dayStr)
            print("Enter month offset (0 for Jan/Apr/Jul/Oct, 1 for Feb/May/Aug/Nov, 2 for Mar/Jun/Sep/Dec): ")
            val offset = readLine()?.toIntOrNull() ?: 0
            com.example.rewire.core.RecurrenceType.QuarterlyByWeekday(week, day, offset)
        }
        7 -> {
            println("Enter days of week separated by commas (e.g., MONDAY,THURSDAY): ")
            val daysStr = readLine()?.split(",")?.map { it.trim().uppercase() } ?: listOf("MONDAY")
            val days = daysStr.map { com.example.rewire.core.DayOfWeek.valueOf(it) }
            com.example.rewire.core.RecurrenceType.CustomWeekly(days)
        }
        else -> com.example.rewire.core.RecurrenceType.Daily
    }
    var customDays: Set<com.example.rewire.core.DayOfWeek>? = null // legacy, not used with new types
    print("Start date (YYYY-MM-DD, leave blank for today): ")
    val startDateInput = readLine()?.takeIf { it.isNotBlank() }
    val startDate = try { startDateInput?.let { java.time.LocalDate.parse(it) } ?: java.time.LocalDate.now() } catch (e: Exception) { java.time.LocalDate.now() }
    print("Preferred time (HH:mm): ")
    val timeInput = readLine()?.takeIf { it.isNotBlank() }
    val preferredTime = try { timeInput?.let { LocalTime.parse(it) } ?: LocalTime.of(9, 0) } catch (e: Exception) { LocalTime.of(9, 0) }
    print("Estimated time in minutes: ")
    val minsInput = readLine()?.toIntOrNull() ?: 0
    manager.addHabit(
        Habit(
            name = name,
            recurrence = recurrence,
            preferredTime = preferredTime,
            estimatedMinutes = minsInput,
            customDays = customDays,
            startDate = startDate
            // id uses default
        )
    )
    println("Habit '$name' added.")
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
    if (habit.recurrence is com.example.rewire.core.RecurrenceType.CustomWeekly) {
        println("Custom days: ${habit.customDays}")
    }
    println("Preferred time: ${habit.preferredTime}")
    println("Estimated minutes: ${habit.estimatedMinutes}")
    val notes = manager.getNotes(habitName)
    println("Notes:")
    if (notes.isEmpty()) {
        println("  (none)")
    } else {
        notes.forEach { note -> println("  ${note.date}: ${note.text}") }
    }
        // Completions are managed by the app module (RoomDB). Not available in core logic.
}

fun cliEditHabit(manager: HabitManager, habitName: String): String? {
    val oldName = habitName
    val habit = manager.getHabit(oldName) ?: return null

    println("Editing habit: $oldName")
    print("New name (leave blank to keep '${habit.name}'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }

    val recurrenceOptions = listOf(
        "Daily",
        "Weekly",
        "Monthly by date",
        "Monthly by weekday",
        "Quarterly by date",
        "Quarterly by weekday",
        "Custom weekly"
    )
    println("Select new recurrence type or leave blank:")
    recurrenceOptions.forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("New recurrence type (1-${recurrenceOptions.size}): ")
    val recInput = readLine()?.toIntOrNull()
    val newRecurrence = when (recInput) {
        1 -> com.example.rewire.core.RecurrenceType.Daily
        2 -> com.example.rewire.core.RecurrenceType.Weekly
        3 -> {
            print("Enter day of month (1-31): ")
            val day = readLine()?.toIntOrNull() ?: 1
            com.example.rewire.core.RecurrenceType.MonthlyByDate(day)
        }
        4 -> {
            print("Enter week of month (1-5): ")
            val week = readLine()?.toIntOrNull() ?: 1
            print("Enter day of week (e.g., MONDAY): ")
            val dayStr = readLine()?.uppercase() ?: "MONDAY"
            val day = com.example.rewire.core.DayOfWeek.valueOf(dayStr)
            com.example.rewire.core.RecurrenceType.MonthlyByWeekday(week, day)
        }
        5 -> {
            print("Enter day of month (1-31): ")
            val day = readLine()?.toIntOrNull() ?: 1
            print("Enter month offset (0 for Jan/Apr/Jul/Oct, 1 for Feb/May/Aug/Nov, 2 for Mar/Jun/Sep/Dec): ")
            val offset = readLine()?.toIntOrNull() ?: 0
            com.example.rewire.core.RecurrenceType.QuarterlyByDate(day, offset)
        }
        6 -> {
            print("Enter week of month (1-5): ")
            val week = readLine()?.toIntOrNull() ?: 1
            print("Enter day of week (e.g., MONDAY): ")
            val dayStr = readLine()?.uppercase() ?: "MONDAY"
            val day = com.example.rewire.core.DayOfWeek.valueOf(dayStr)
            print("Enter month offset (0 for Jan/Apr/Jul/Oct, 1 for Feb/May/Aug/Nov, 2 for Mar/Jun/Sep/Dec): ")
            val offset = readLine()?.toIntOrNull() ?: 0
            com.example.rewire.core.RecurrenceType.QuarterlyByWeekday(week, day, offset)
        }
        7 -> {
            println("Enter days of week separated by commas (e.g., MONDAY,THURSDAY): ")
            val daysStr = readLine()?.split(",")?.map { it.trim().uppercase() } ?: listOf("MONDAY")
            val days = daysStr.map { com.example.rewire.core.DayOfWeek.valueOf(it) }
            com.example.rewire.core.RecurrenceType.CustomWeekly(days)
        }
        else -> null
    }
    var newCustomDays: Set<com.example.rewire.core.DayOfWeek>? = habit.customDays // legacy, not used with new types

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
    println("Habit updated.")
    return newName ?: oldName
}


// =====================
// Habit Note Functions (CRUD)
// =====================

fun cliAddHabitNote(manager: HabitManager, habitName: String) {
    print("Enter date for note (YYYY-MM-DD, leave blank for today): ")
    val dateStr = readLine()
    val date = if (dateStr.isNullOrBlank()) java.time.LocalDate.now() else try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    val notes = manager.getNotes(habitName)
    if (notes.any { it.date == date }) {
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
    println("Note added to '$habitName'.")
}

fun cliListHabitNotes(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes for '$habitName':")
    notes.forEachIndexed { i, note -> println("${i + 1}. ${note.date}: ${note.text}") }
}

fun cliDeleteHabitNote(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes:")
    notes.forEachIndexed { i, note -> println("${i + 1}. ${note.date}: ${note.text}") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    val date = notes[noteIndex - 1].date
    manager.deleteNote(habitName, date)
    println("Note deleted.")
}

fun cliEditHabitNote(manager: HabitManager, habitName: String) {
    val notes = manager.getNotes(habitName)
    if (notes.isEmpty()) {
        println("No notes for '$habitName'.")
        return
    }
    println("Notes:")
    notes.forEachIndexed { i, note -> println("${i + 1}. ${note.date}: ${note.text}") }
    print("Select note to edit (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size }
    if (noteIndex == null) {
        println("Invalid selection.")
        return
    }
    val date = notes[noteIndex - 1].date
    print("Enter new note: ")
    val newNote = readLine()?.takeIf { it.isNotBlank() }
    if (newNote == null) {
        println("Note cannot be blank.")
        return
    }
    manager.editNote(habitName, date, newNote)
    println("Note updated.")
}

fun getHabitNoteDays(manager: HabitManager, habitName: String): List<java.time.LocalDate> {
    return manager.getNoteDays(habitName)
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
