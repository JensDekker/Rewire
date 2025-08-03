package com.example.rewire.cli

import java.time.LocalDate
import java.time.LocalTime


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
// Place shared menu logic, input helpers, or utilities here

fun promptInt(message: String, range: IntRange): Int? {
    print("$message ")
    val inputStr = readLine()
    if (inputStr == null) return null
    val input = inputStr.toIntOrNull()
    return if (input != null && input in range) input else null
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
    if (title.isNotBlank()) println(title)
    options.forEachIndexed { i, option -> println("${i + 1}. $option") }
    return promptInt("Select option:", 1..options.size)?.minus(1)
}

fun confirmAction(message: String): Boolean {
    return promptYesNo("$message Are you sure?")
}

fun printDivider() {
    println("====================")
}
