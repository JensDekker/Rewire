package com.example.rewire.cli

import java.time.LocalDate
import java.time.LocalTime
import java.time.DayOfWeek

fun promptDate(message: String): LocalDate? {
    while (true) {
        print("$message (YYYY-MM-DD): ")
        val input = readLine()
        try {
            if (input != null && input.isNotBlank()) return LocalDate.parse(input)
        } catch (e: Exception) {
            println("Invalid date format. Please use YYYY-MM-DD.")
        }
    }
}

fun promptTime(message: String): LocalTime? {
    while (true) {
        print("$message (HH:mm): ")
        val input = readLine()
        try {
            if (input != null && input.isNotBlank()) return LocalTime.parse(input)
        } catch (e: Exception) {
            println("Invalid time format. Please use HH:mm.")
        }
    }
}

fun promptSelection(message: String, options: List<String>): String? {
    println(message)
    options.forEachIndexed { i, option -> println("${i + 1}. $option") }
    val index = promptInt("Select option:", 1..options.size)
    return index?.let { options[it - 1] }
}

fun printError(message: String) {
    println("[ERROR] $message")
}

fun printSuccess(message: String) {
    println("[SUCCESS] $message")
}

fun waitForEnter(message: String = "Press Enter to continue...") {
    print(message)
    readLine()
}
// Common/shared CLI functions and utilities

/**
 * Prompts the user to select days of the week for custom weekly recurrence.
 * Returns a Set<DayOfWeek> or null if cancelled.
 */
fun promptCustomDays(): Set<DayOfWeek>? {
    println("Select days of the week for reminders.")
    println("Enter numbers separated by commas (e.g., 1,3,6 for Monday, Wednesday, Saturday):")
    DayOfWeek.values().forEach { println("${it.value}. ${it}") }
    while (true) {
        print("Days (1=Monday ... 7=Sunday): ")
        val input = readLine()?.trim()
        if (input.isNullOrBlank()) {
            printError("Input cannot be blank. Please enter at least one day.")
            continue
        }
        val dayNumbers = input.split(",").mapNotNull { it.trim().toIntOrNull() }
        val validDays = dayNumbers.filter { it in 1..7 }.map { DayOfWeek.of(it) }.toSet()
        if (validDays.isEmpty()) {
            printError("No valid days entered. Please enter numbers between 1 and 7.")
            continue
        }
        return validDays
    }
}

/**
 * Returns a user-friendly string for a set of DayOfWeek (e.g., "Monday, Wednesday, Saturday").
 */
fun formatCustomDays(days: Set<DayOfWeek>?): String {
    return days?.sortedBy { it.value }?.joinToString(", ") { it.name.capitalize() } ?: "None"
}
// Place shared menu logic, input helpers, or utilities here

fun promptInt(message: String, range: IntRange): Int? {
    while (true) {
        print("$message ")
        val inputStr = readLine()
        if (inputStr == null || inputStr.isBlank()) {
            printError("Input cannot be blank. Please enter a number in range ${range.first} to ${range.last}.")
            continue
        }
        val input = inputStr.toIntOrNull()
        if (input != null && input in range) {
            return input
        } else {
            printError("Invalid input. Please enter a number in range ${range.first} to ${range.last}.")
        }
    }
}

fun promptString(message: String): String? {
    while (true) {
        print("$message ")
        val input = readLine()
        if (input != null && input.isNotBlank()) return input
        println("Input cannot be blank.")
    }
}

fun promptYesNo(message: String): Boolean {
    while (true) {
        print("$message (y/n): ")
        when (readLine()?.trim()?.lowercase()) {
            "y", "yes" -> return true
            "n", "no" -> return false
            else -> println("Please enter 'y' or 'n'.")
        }
    }
}

fun showMenu(options: List<String>, title: String = ""): Int? {
    while (true) {
        if (title.isNotBlank()) println(title)
        options.forEachIndexed { i, option -> println("${i + 1}. $option") }
        val selection = promptInt("Select option:", 1..options.size)
        if (selection != null) return selection - 1
    }
}

fun confirmAction(message: String): Boolean {
    return promptYesNo("$message Are you sure?")
}

fun printDivider() {
    println("====================")
}
