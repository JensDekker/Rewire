import java.time.*

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
        timeInput?.let { LocalTime.parse(it) }
    } catch (e: Exception) {
        null
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
