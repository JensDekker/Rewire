package com.example.rewire.core

import java.time.LocalDate

class AddictionManager {
    fun updateUsagePlanItem(name: String, index: Int, updatedGoal: AbstinenceGoal) {
        getAddiction(name)?.let {
            val plan = it.usagePlan.toMutableList()
            if (index in plan.indices) {
                plan[index] = updatedGoal
                deleteAddiction(name)
                addAddiction(it.copy(usagePlan = plan))
            }
        }
    }

    fun deleteUsagePlan(name: String) {
        getAddiction(name)?.let {
            deleteAddiction(name)
            addAddiction(it.copy(usagePlan = emptyList()))
        }
    }
    private val addictions = mutableListOf<AddictionHabit>()

    fun addAddiction(habit: AddictionHabit) = addictions.add(habit)
    fun deleteAddiction(name: String) = addictions.removeIf { it.name == name }
    fun getAddiction(name: String): AddictionHabit? = addictions.find { it.name == name }
    fun listAddictions(): List<String> = addictions.map { it.name }
    fun renameAddiction(oldName: String, newName: String) { getAddiction(oldName)?.let { deleteAddiction(oldName); addAddiction(it.copy(name = newName)) } }
    fun replaceUsagePlan(name: String, newUsagePlan: List<AbstinenceGoal>) = getAddiction(name)?.let { deleteAddiction(name); addAddiction(it.copy(usagePlan = newUsagePlan)) }
    fun insertUsagePlanItem(name: String, index: Int, newGoal: AbstinenceGoal) {
        getAddiction(name)?.let {
            val updated = it.copy(usagePlan = it.usagePlan.toMutableList().apply { add(index, newGoal) })
            deleteAddiction(name); addAddiction(updated)
        }
    }
    fun reorderUsagePlan(name: String, fromIndex: Int, toIndex: Int) {
        getAddiction(name)?.let {
            val g = it.usagePlan.toMutableList()
            val goal = g.removeAt(fromIndex)
            g.add(toIndex, goal)
            deleteAddiction(name); addAddiction(it.copy(usagePlan = g))
        }
    }
    fun deleteUsagePlanItem(name: String, index: Int) {
        getAddiction(name)?.let {
            val updated = it.usagePlan.toMutableList().apply { removeAt(index) }
            deleteAddiction(name); addAddiction(it.copy(usagePlan = updated))
        }
    }
    fun printUsagePlan(name: String) {
        getAddiction(name)?.usagePlan?.forEachIndexed { i, g -> println("${i + 1}. ${g.value} rest day(s) / ${g.period}, x${g.repeatCount}") }
    }
    fun printAddictionStats(name: String, date: LocalDate = LocalDate.now()) {
        val addiction = getAddiction(name) ?: return
        println("Addiction: ${addiction.name}")
        println("Log:")
        addiction.getDailyUseSummary().forEach { (d, count) -> println("$d: $count use(s)") }
        val goal = addiction.getCurrentGoal(date)
        println("Current goal: ${goal?.value ?: "-"} rest day(s) per ${goal?.period}")
        println("Goal met: ${addiction.isGoalMet(date)}")
    }

    fun logUsage(name: String, date: LocalDate = LocalDate.now()) {
        getAddiction(name)?.logUse(date)
    }

    fun addNote(name: String, date: LocalDate, note: String) {
        getAddiction(name)?.addNote(date, note)
    }

    fun getNotes(name: String): Map<LocalDate, String> {
        return getAddiction(name)?.dailyNotes ?: emptyMap()
    }

    fun editNote(name: String, date: LocalDate, newNote: String) {
        getAddiction(name)?.editNote(date, newNote)
    }

    fun deleteNote(name: String, date: LocalDate) {
        getAddiction(name)?.deleteNote(date)
    }

    fun getUsagePlan(name: String): List<AbstinenceGoal> {
        return getAddiction(name)?.usagePlan ?: emptyList()
    }

    fun addUsagePlanItem(name: String, goal: AbstinenceGoal) {
        getAddiction(name)?.addUsagePlanItem(goal)
    }

    fun editUsagePlanItem(name: String, index: Int, updatedGoal: AbstinenceGoal) {
        getAddiction(name)?.editUsagePlanItem(index, updatedGoal)
    }

    fun clearUsagePlan(name: String) {
        getAddiction(name)?.let {
            deleteAddiction(name)
            addAddiction(it.copy(usagePlan = emptyList()))
        }
    }
}
