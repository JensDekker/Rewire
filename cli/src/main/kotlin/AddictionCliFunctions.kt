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
    manager.addAddiction(AddictionHabit(name, LocalDate.now()))
    println("✅ Addiction '$name' added.")
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

fun cliViewAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to view.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to view (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    manager.printAddictionStats(addictions[index - 1])
}

fun cliEditAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to edit.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to edit (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val oldName = addictions[index - 1]
    print("New name (leave blank to keep '$oldName'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }
    manager.renameAddiction(oldName, newName ?: oldName)
    println("✏️ Addiction updated.")
}

fun cliDeleteAddiction(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to delete.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction to delete (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val nameToDelete = addictions[index - 1]
    manager.deleteAddiction(nameToDelete)
    println("🗑️ Addiction '$nameToDelete' deleted.")
}

fun cliLogAddictionUsage(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to log usage for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter usage date (YYYY-MM-DD): ")
    val dateStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val date = try { java.time.LocalDate.parse(dateStr) } catch (e: Exception) { println("Invalid date format."); return }
    manager.logUsage(name, date)
    println("📋 Usage for '$name' logged.")
}

// =====================
// Addiction Note Functions (CRUD)
// =====================

fun cliAddAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to add notes to.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter note: ")
    val note = readLine()?.takeIf { it.isNotBlank() } ?: return
    val date = LocalDate.now()
    manager.addNote(name, date, note)
    println("📝 Note added to '$name'.")
}

fun cliListAddictionNotes(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to list notes for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    println("Notes for '$name':")
    notes.entries.forEachIndexed { i: Int, entry: Map.Entry<LocalDate, String> -> println("${i + 1}. ${entry.key}: ${entry.value}") }
}

fun cliEditAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to edit notes for.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    val noteDates = notes.keys.toList()
    println("Notes:")
    noteDates.forEachIndexed { i: Int, date: LocalDate -> println("${i + 1}. $date: ${notes[date]}") }
    print("Select note to edit (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..noteDates.size } ?: return
    val date = noteDates[noteIndex - 1]
    print("Enter new note: ")
    val newNote = readLine()?.takeIf { it.isNotBlank() } ?: return
    manager.editNote(name, date, newNote)
    println("✏️ Note updated.")
}

fun cliDeleteAddictionNote(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions to delete notes from.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val notes = manager.getNotes(name)
    if (notes.isEmpty()) {
        println("No notes for '$name'.")
        return
    }
    val noteDates = notes.keys.toList()
    println("Notes:")
    noteDates.forEachIndexed { i: Int, date: LocalDate -> println("${i + 1}. $date: ${notes[date]}") }
    print("Select note to delete (number): ")
    val noteIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..noteDates.size } ?: return
    val date = noteDates[noteIndex - 1]
    manager.deleteNote(name, date)
    println("🗑️ Note deleted.")
}

// =====================
// Usage Plan Functions (CRUD)
// =====================

fun cliListUsagePlan(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan for '$name':")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
}

fun cliAddUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    print("Enter goal description: ")
    val description = readLine()?.takeIf { it.isNotBlank() } ?: return
    print("Enter goal period (e.g., DAILY, WEEKLY, MONTHLY): ")
    val periodStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val period = try { com.example.rewire.core.GoalPeriod.valueOf(periodStr.uppercase()) } catch (e: Exception) { println("Invalid period."); return }
    print("Enter target count (integer): ")
    val count = readLine()?.toIntOrNull() ?: return
    // Prompt for value and repeatCount
    print("Enter goal value (integer): ")
    val value = readLine()?.toIntOrNull() ?: return
    print("Enter repeat count (integer, default 1): ")
    val repeatCountInput = readLine()
    val repeatCount = repeatCountInput?.toIntOrNull() ?: 1
    val goal = com.example.rewire.core.AbstinenceGoal(period, value, repeatCount)
    manager.addUsagePlanItem(name, goal)
    println("✅ Usage plan item added to '$name'.")
}

fun cliEditUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
    print("Select item to edit (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    print("Enter new goal description: ")
    val description = readLine()?.takeIf { it.isNotBlank() } ?: return
    print("Enter new goal period (e.g., DAILY, WEEKLY, MONTHLY): ")
    val periodStr = readLine()?.takeIf { it.isNotBlank() } ?: return
    val period = try { com.example.rewire.core.GoalPeriod.valueOf(periodStr.uppercase()) } catch (e: Exception) { println("Invalid period."); return }
    print("Enter new target count (integer): ")
    val count = readLine()?.toIntOrNull() ?: return
    // Prompt for value and repeatCount
    print("Enter new goal value (integer): ")
    val value = readLine()?.toIntOrNull() ?: return
    print("Enter new repeat count (integer, default 1): ")
    val repeatCountInput = readLine()
    val repeatCount = repeatCountInput?.toIntOrNull() ?: 1
    val goal = com.example.rewire.core.AbstinenceGoal(period, value, repeatCount)
    manager.editUsagePlanItem(name, itemIndex - 1, goal)
    println("✏️ Usage plan item updated.")
}

fun cliDeleteUsagePlanItem(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    val plan = manager.getUsagePlan(name)
    if (plan.isEmpty()) {
        println("No usage plan for '$name'.")
        return
    }
    println("Usage plan items:")
    plan.forEachIndexed { i, item -> println("${i + 1}. $item") }
    print("Select item to delete (number): ")
    val itemIndex = readLine()?.toIntOrNull()?.takeIf { it in 1..plan.size } ?: return
    manager.deleteUsagePlanItem(name, itemIndex - 1)
    println("🗑️ Usage plan item deleted.")
}

fun cliClearUsagePlan(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        println("No addictions found.")
        return
    }
    println("Available addictions:")
    addictions.forEachIndexed { i, name -> println("${i + 1}. $name") }
    print("Select addiction (number): ")
    val index = readLine()?.toIntOrNull()?.takeIf { it in 1..addictions.size } ?: return
    val name = addictions[index - 1]
    manager.clearUsagePlan(name)
    println("🧹 Usage plan for '$name' cleared.")
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
