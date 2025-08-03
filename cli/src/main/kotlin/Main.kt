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
    val index = promptInt("Select habit (number):", 1..habits.size) ?: return
    val name = habits[index - 1]
    var running = true
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
            "Habit: $name"
        )
        when (choice) {
            0 -> cliEditHabit(manager)
            1 -> cliShowHabitDetails(manager)
            2 -> cliDeleteHabit(manager)
            3 -> cliCompleteHabit(manager)
            4 -> habitNotesMenu(manager, name)
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
            0 -> cliListHabitNotes(manager)
            1 -> cliAddHabitNote(manager)
            2 -> cliEditHabitNote(manager)
            3 -> cliDeleteHabitNote(manager)
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
    val index = promptInt("Select addiction (number):", 1..addictions.size) ?: return
    val name = addictions[index - 1]
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
            "Addiction: $name"
        )
        when (choice) {
            0 -> cliViewAddiction(manager)
            1 -> cliEditAddiction(manager)
            2 -> cliDeleteAddiction(manager)
            3 -> cliLogAddictionUsage(manager)
            4 -> addictionNotesMenu(manager, name)
            5 -> usagePlanMenu(manager, name)
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
                "Back"
            ),
            "Notes for Addiction: $addictionName"
        )
        when (choice) {
            0 -> cliListAddictionNotes(manager)
            1 -> cliAddAddictionNote(manager)
            2 -> cliEditAddictionNote(manager)
            3 -> cliDeleteAddictionNote(manager)
            4 -> running = false
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
            0 -> cliListUsagePlan(manager)
            1 -> cliAddUsagePlanItem(manager)
            2 -> cliEditUsagePlanItem(manager)
            3 -> cliDeleteUsagePlanItem(manager)
            4 -> cliClearUsagePlan(manager)
            5 -> running = false
        }
    }
}
