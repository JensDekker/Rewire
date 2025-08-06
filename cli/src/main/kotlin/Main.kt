package com.example.rewire.cli

import java.time.*
import com.example.rewire.core.HabitManager
import com.example.rewire.core.AddictionManager
import com.example.rewire.cli.*

fun main() {
    val habitManager = HabitManager()
    val addictionManager = AddictionManager()
    var running = true
    while (running) {
        printDivider()
        println("Main Menu")
        val mainChoice = showMenu(
            listOf("Habits", "Addictions", "Exit"),
            "Select a category:"
        )
        if (mainChoice == null) {
            println("No input or invalid selection. Exiting menu.")
            break
        }
        when (mainChoice) {
            0 -> habitsMenu(habitManager)
            1 -> addictionsMenu(addictionManager)
            2 -> running = false
        }
    }
    println("Goodbye!")
}

fun habitsMenu(manager: HabitManager) {
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "List Habits",
                "Add Habit",
                "Select Habit",
                "Back"
            ),
            "Habits Menu"
        )
        when (choice) {
            0 -> manager.listHabits().let {
                printDivider()
                if (it.isEmpty()) printError("No habits found.")
                else it.forEachIndexed { i, name -> println("${i + 1}. $name") }
            }
            1 -> cliAddHabit(manager)
            2 -> selectHabitMenu(manager)
            3 -> running = false
        }
    }
}

fun selectHabitMenu(manager: HabitManager) {
    val habits = manager.listHabits()
    if (habits.isEmpty()) {
        printError("No habits to select.")
        return
    }
    printDivider()
    println("Available habits:")
    habits.forEachIndexed { i, habitName -> println("${i + 1}. $habitName") }
    val index = promptInt("Select habit (number):", 1..habits.size) ?: return
    val selectedHabitName = habits[index - 1]
    var running = true
    var currentHabitName = selectedHabitName
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "Edit Habit",
                "Show Habit Details",
                "Delete Habit",
                "Complete Habit",
                "Habit Notes",
                "Back"
            ),
            "Habit: $currentHabitName"
        )
        when (choice) {
            0 -> {
                val oldName = currentHabitName
                val newName = cliEditHabit(manager, oldName)
                if (newName != null && newName.isNotBlank() && newName != oldName) {
                    currentHabitName = newName
                }
            }
            1 -> cliShowHabitDetails(manager, currentHabitName)
            2 -> {
                cliDeleteHabit(manager, currentHabitName)
                running = false // Return to habits menu after deletion
            }
            3 -> cliCompleteHabit(manager, currentHabitName)
            4 -> habitNotesMenu(manager, currentHabitName)
            5 -> running = false
        }
    }
}

fun habitNotesMenu(manager: HabitManager, habitName: String) {
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "List Notes",
                "Add Note",
                "Edit Note",
                "Delete Note",
                "Back"
            ),
            "Notes for Habit: $habitName"
        )
        when (choice) {
            0 -> cliListHabitNotes(manager, habitName)
            1 -> cliAddHabitNote(manager, habitName)
            2 -> cliEditHabitNote(manager, habitName)
            3 -> cliDeleteHabitNote(manager, habitName)
            4 -> running = false
        }
    }
}

fun addictionsMenu(manager: AddictionManager) {
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "List Addictions",
                "Add Addiction",
                "Select Addiction",
                "Back"
            ),
            "Addictions Menu"
        )
        when (choice) {
            0 -> manager.listAddictions().let {
                if (it.isEmpty()) printError("No addictions found.")
                else it.forEachIndexed { i, name -> println("${i + 1}. $name") }
            }
            1 -> cliAddAddiction(manager)
            2 -> selectAddictionMenu(manager)
            3 -> running = false
        }
    }
}

fun selectAddictionMenu(manager: AddictionManager) {
    val addictions = manager.listAddictions()
    if (addictions.isEmpty()) {
        printError("No addictions to select.")
        return
    }
    printDivider()
    println("Available addictions:")
    addictions.forEachIndexed { i, addictionName -> println("${i + 1}. $addictionName") }
    val index = promptInt("Select addiction (number):", 1..addictions.size) ?: return
    var currentAddictionName = addictions[index - 1]
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "View Addiction Details",
                "Edit Addiction",
                "Delete Addiction",
                "Log Usage",
                "Addiction Notes",
                "Usage Plan",
                "Back"
            ),
            "Addiction: $currentAddictionName"
        )
        when (choice) {
            0 -> cliViewAddiction(manager, currentAddictionName)
            1 -> {
                val oldName = currentAddictionName
                print("New name (leave blank to keep '$oldName'): ")
                val newName = readLine()?.takeIf { it.isNotBlank() }
                manager.renameAddiction(oldName, newName ?: oldName)
                println("✏️ Addiction updated.")
                currentAddictionName = newName ?: oldName
            }
            2 -> {
                cliDeleteAddiction(manager, currentAddictionName)
                running = false // Return to addictions menu after deletion
            }
            3 -> cliLogAddictionUsage(manager, currentAddictionName)
            4 -> addictionNotesMenu(manager, currentAddictionName)
            5 -> usagePlanMenu(manager, currentAddictionName)
            6 -> running = false
        }
    }
}

fun addictionNotesMenu(manager: AddictionManager, addictionName: String) {
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "List Notes",
                "Add Note",
                "Edit Note",
                "Delete Note",
                "Show Note Days",
                "Back"
            ),
            "Notes for Addiction: $addictionName"
        )
        when (choice) {
            0 -> cliListAddictionNotes(manager, addictionName)
            1 -> cliAddAddictionNote(manager, addictionName)
            2 -> cliEditAddictionNote(manager, addictionName)
            3 -> cliDeleteAddictionNote(manager, addictionName)
            4 -> {
                val days = getAddictionNoteDays(manager, addictionName)
                if (days.isEmpty()) println("No days with notes for '$addictionName'.")
                else println("Days with notes for '$addictionName':\n" + days.joinToString(", "))
            }
            5 -> running = false
        }
    }
}

fun usagePlanMenu(manager: AddictionManager, addictionName: String) {
    var running = true
    while (running) {
        printDivider()
        val choice = showMenu(
            listOf(
                "List Plan Items",
                "Add Item",
                "Edit Item",
                "Delete Item",
                "Clear Plan",
                "Back"
            ),
            "Usage Plan for Addiction: $addictionName"
        )
        when (choice) {
            0 -> cliListUsagePlan(manager, addictionName)
            1 -> cliAddUsagePlanItem(manager, addictionName)
            2 -> cliEditUsagePlanItem(manager, addictionName)
            3 -> cliDeleteUsagePlanItem(manager, addictionName)
            4 -> cliClearUsagePlan(manager, addictionName)
            5 -> running = false
        }
    }
}
