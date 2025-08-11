package com.example.rewire.cli

import java.time.LocalDate
import com.example.rewire.core.AddictionManager
import com.example.rewire.core.AddictionHabit

// =====================
// Addiction Functions (CRUD)
// =====================

fun cliAddAddiction(manager: AddictionManager) {
    print("Enter addiction name: ")
    val name = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.addAddiction(
        AddictionHabit(
            name = name,
            startDate = LocalDate.now()
            // id, recurrence, preferredTime, estimatedMinutes use defaults
        )
    )
    println("Addiction '$name' added.")
}

fun cliListAddictions(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
}

fun cliViewAddiction(manager: AddictionManager, addictionName: String? = null) {
    val nameToView = addictionName ?: run {
        val addictions = manager.listAddictions()
        if (addictions.isEmpty()) {
            println("No addictions to view.")
            return
        }
        println("Available addictions:")
        addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
        print("Select addiction to view (number): ")
        val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
        addictions[index - 1]
    }
    manager.printAddictionStats(nameToView)
    // Show notes for the addiction, similar to habits
    val notes = manager.getNotes(nameToView)
    println("Notes:")
    if (notes.isEmpty()) {
        println("  (none)")
    } else {
        notes.forEach { note -> println("  ${note.date}: ${note.text}") }
    }
}

fun cliEditAddiction(manager: AddictionManager, addictionName: String? = null) {
    val nameToEdit = addictionName ?: run {
        val addictions = manager.listAddictions()
        if (addictions.isEmpty()) {
            println("No addictions to edit.")
            return
        }
        println("Available addictions:")
        addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
        print("Select addiction to edit (number): ")
        val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
        addictions[index - 1]
    }
    print("New name (leave blank to keep '$nameToEdit'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }
    manager.renameAddiction(nameToEdit, newName ?: nameToEdit)
    println("Addiction updated.")
}

fun cliDeleteAddiction(manager: AddictionManager, addictionName: String? = null) {
    val nameToDelete = addictionName ?: run {
        val addictions = manager.listAddictions()
        if (addictions.isEmpty()) {
            println("No addictions to delete.")
            return
        }
        println("Available addictions:")
        addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
        print("Select addiction to delete (number): ")
        val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
        addictions[index - 1]
    }
    manager.deleteAddiction(nameToDelete)
    println("Addiction '$nameToDelete' deleted.")
}

fun cliLogAddictionUsage(manager: AddictionManager, addictionName: String? = null) {
    val name = addictionName ?: run {
        val addictions = manager.listAddictions()
        if (addictions.isEmpty()) {
            println("No addictions to log usage for.")
            return
        }
        println("Available addictions:")
        addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
        print("Select addiction (number): ")
        val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
        addictions[index - 1]
    }
    print("Enter usage date (YYYY-MM-DD, leave blank for today): ")
    val dateStr = readLine()
    val date = if (dateStr.isNullOrBlank()) java.time.LocalDate.now() else try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    print("Enter usage time (HH:mm, leave blank to not log time): ")
    val timeStr = readLine()
    val time = if (timeStr.isNullOrBlank()) null else try { java.time.LocalTime.parse(timeStr) } catch (e: Exception) { println("Invalid time format."); return }
    manager.logUsage(name, date, time)
    println("📋 Usage for '$name' logged." + if (time != null) " at $time" else "")
}

// =====================
// Addiction Note Functions (CRUD)
// =====================

fun cliAddAddictionNote(manager: AddictionManager, addictionName: String) {
    print("Enter date for note (YYYY-MM-DD, leave blank for today): ")
    val dateStr = readLine()
    val date = if (dateStr.isNullOrBlank()) java.time.LocalDate.now() else try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    val notes = manager.getNotes(addictionName)
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
    manager.addNote(addictionName, date, note)
    println("Note added to '$addictionName'.")
}

fun cliListAddictionNotes(manager: AddictionManager, addictionName: String) {
    val notes = manager.getNotes(addictionName)
    if (notes.isEmpty()) {
        println("No notes for '$addictionName'.")
        return
    }
    println("Notes for '$addictionName':")
    notes.forEachIndexed { i, note -> println("${i + 1}. ${note.date}: ${note.text}") }
}

fun cliEditAddictionNote(manager: AddictionManager, addictionName: String) {
    val notes = manager.getNotes(addictionName)
    if (notes.isEmpty()) {
        println("No notes for '$addictionName'.")
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
    manager.editNote(addictionName, date, newNote)
    println("Note updated.")
}

fun cliDeleteAddictionNote(manager: AddictionManager, addictionName: String) {
    val notes = manager.getNotes(addictionName)
    if (notes.isEmpty()) {
        println("No notes for '$addictionName'.")
        return
    }
    println("Notes:")
    notes.forEachIndexed { i, note -> println("${i + 1}. ${note.date}: ${note.text}") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..notes.size } ?: return
    val date = notes[noteIndex - 1].date
    manager.deleteNote(addictionName, date)
    println("Note deleted.")
}

// =====================
// Usage Plan Functions (CRUD)
// =====================

fun cliListUsagePlan(manager: AddictionManager, addictionName: String) {
    val plan = manager.getUsagePlan(addictionName)
    if (plan.isEmpty()) {
        println("No usage plan for '$addictionName'.")
        return
    }
    println("Usage plan for '$addictionName':")
    plan.forEachIndexed { i, item ->
        println("${i + 1}. ${item.value} use(s) permitted per ${item.recurrence}, x${item.repeatCount}")
    }
}

fun cliAddUsagePlanItem(manager: AddictionManager, addictionName: String) {
    print("Enter recurrence type (DAILY, WEEKLY, MONTHLY, QUARTERLY, WEEKDAYS, WEEKENDS, CUSTOM_WEEKLY): ")
    val recurrenceStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val recurrence = try { com.example.rewire.core.RecurrenceType.valueOf(recurrenceStr.uppercase()) } catch (e: Exception) { println("Invalid recurrence type."); return }
    print("Enter permitted uses per recurrence (integer): ")
    val value = readLine()?.toIntOrNull() ?: return
    print("Enter repeat count (integer, default 1): ")
    val repeatCountInput = readLine()
    val repeatCount = repeatCountInput?.toIntOrNull() ?: 1
    val goal = com.example.rewire.core.AbstinenceGoal(
        recurrence = recurrence,
        value = value,
        repeatCount = repeatCount
        // id and addictionId use defaults
    )
    manager.addUsagePlanItem(addictionName, goal)
    println("Usage plan item added to '$addictionName'.")
}

fun cliEditUsagePlanItem(manager: AddictionManager, addictionName: String) {
    val plan = manager.getUsagePlan(addictionName)
    if (plan.isEmpty()) {
        println("No usage plan for '$addictionName'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item ->
        println("${i + 1}. ${item.value} use(s) permitted per ${item.recurrence}, x${item.repeatCount}")
    }
    print("Select item to edit (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    print("Enter new recurrence type (DAILY, WEEKLY, MONTHLY, QUARTERLY, WEEKDAYS, WEEKENDS, CUSTOM_WEEKLY): ")
    val recurrenceStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val recurrence = try { com.example.rewire.core.RecurrenceType.valueOf(recurrenceStr.uppercase()) } catch (e: Exception) { println("Invalid recurrence type."); return }
    print("Enter new permitted uses per recurrence (integer): ")
    val value = readLine()?.toIntOrNull() ?: return
    print("Enter new repeat count (integer, default 1): ")
    val repeatCountInput = readLine()
    val repeatCount = repeatCountInput?.toIntOrNull() ?: 1
    val goal = com.example.rewire.core.AbstinenceGoal(
        recurrence = recurrence,
        value = value,
        repeatCount = repeatCount
        // id and addictionId use defaults
    )
    manager.editUsagePlanItem(addictionName, itemIndex - 1, goal)
    println("Usage plan item updated.")
}

fun cliDeleteUsagePlanItem(manager: AddictionManager, addictionName: String) {
    val plan = manager.getUsagePlan(addictionName)
    if (plan.isEmpty()) {
        println("No usage plan for '$addictionName'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
    print("Select item to delete (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    manager.deleteUsagePlanItem(addictionName, itemIndex - 1)
    println("Usage plan item deleted.")
}

fun cliClearUsagePlan(manager: AddictionManager, addictionName: String) {
    manager.clearUsagePlan(addictionName)
    println("Usage plan for '$addictionName' cleared.")
}

fun getAddictionNoteDays(manager: AddictionManager, addictionName: String): List<java.time.LocalDate> {
    return manager.getNoteDays(addictionName)
}


// CLI functions for addiction habits, addiction notes, and usage plans
// Move all addiction-related CLI functions here from CliFunctions.kt

// Example stub
// fun cliAddAddiction(manager: AddictionManager) { /* ... */ }
// fun cliListAddictions(manager: AddictionManager) { /* ... */ }
// fun cliViewAddiction(manager: AddictionManager) { /* ... */ }
// fun cliEditAddiction(manager: AddictionManager) { /* ... */ }
// fun cliDeleteAddiction(manager: AddictionManager) { /* ... */ }
// fun cliAddAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliListAddictionNotes(manager: AddictionManager) { /* ... */ }
// fun cliEditAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliDeleteAddictionNote(manager: AddictionManager) { /* ... */ }
// fun cliListUsagePlan(manager: AddictionManager) { /* ... */ }
// fun cliAddUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliEditUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliDeleteUsagePlanItem(manager: AddictionManager) { /* ... */ }
// fun cliClearUsagePlan(manager: AddictionManager) { /* ... */ }
