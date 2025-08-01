import java.time.*
import java.time.temporal.ChronoUnit

// --- Enums ---
enum class RecurrenceType {
    DAILY, WEEKLY, MONTHLY, QUARTERLY, WEEKDAYS, WEEKENDS
}

enum class GoalPeriod {
    WEEKLY, MONTHLY
}

// --- Habit Class ---
data class Habit(
    val name: String,
    val recurrence: RecurrenceType = RecurrenceType.DAILY,
    val preferredTime: LocalTime = LocalTime.of(9, 0),
    val estimatedMinutes: Int = 10,
    val notes: MutableMap<LocalDate, String> = mutableMapOf(),
    val completions: MutableSet<LocalDate> = mutableSetOf()
) {
    fun isDueOn(date: LocalDate): Boolean = when (recurrence) {
        RecurrenceType.DAILY -> true
        RecurrenceType.WEEKLY -> date.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        RecurrenceType.MONTHLY -> date.dayOfMonth == 1
        RecurrenceType.QUARTERLY -> date.dayOfMonth == 1 && date.month.value % 3 == 1
        RecurrenceType.WEEKENDS -> date.dayOfWeek in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        RecurrenceType.WEEKDAYS -> date.dayOfWeek !in listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
    }

    fun markComplete(date: LocalDate = LocalDate.now()) {
        completions.add(date)
    }

    fun isComplete(date: LocalDate = LocalDate.now()): Boolean = completions.contains(date)

    fun addNote(date: LocalDate, note: String) {
        notes[date] = note
    }

    fun editNote(date: LocalDate, newNote: String) {
        if (notes.containsKey(date)) {
            notes[date] = newNote
        }
    }

    fun deleteNote(date: LocalDate) {
        notes.remove(date)
    }
}

// --- Habit Manager ---
class HabitManager {
    private val habits = mutableListOf<Habit>()

    fun addHabit(habit: Habit) = habits.add(habit)
    fun deleteHabit(name: String) = habits.removeIf { it.name == name }
    fun getHabit(name: String): Habit? = habits.find { it.name == name }
    fun listHabits(): List<String> = habits.map { it.name }

    fun updateHabit(
        name: String,
        newName: String?,
        newRecurrence: RecurrenceType?,
        newPreferredTime: LocalTime?,
        newEstimatedMinutes: Int?
    ) {
        getHabit(name)?.let { old ->
            val updated = old.copy(
                name = newName ?: old.name,
                recurrence = newRecurrence ?: old.recurrence,
                preferredTime = newPreferredTime ?: old.preferredTime,
                estimatedMinutes = newEstimatedMinutes ?: old.estimatedMinutes
            )
            habits.remove(old)
            habits.add(updated)
        }
    }

    fun getHabitsForDate(date: LocalDate): List<Habit> = habits.filter { it.isDueOn(date) }.sortedBy { it.preferredTime }
    fun markHabitComplete(name: String, date: LocalDate = LocalDate.now()) = getHabit(name)?.markComplete(date)
    fun addNote(name: String, date: LocalDate, note: String) = getHabit(name)?.addNote(date, note)

    fun editNote(name: String, date: LocalDate, newNote: String) = getHabit(name)?.editNote(date, newNote)

    fun deleteNote(name: String, date: LocalDate) = getHabit(name)?.deleteNote(date)
    fun getCompletions(name: String): Set<LocalDate> = getHabit(name)?.completions ?: emptySet()
    fun getNotes(name: String): Map<LocalDate, String> = getHabit(name)?.notes ?: emptyMap()
}

// --- Addiction Goal and Habit ---
data class AbstinenceGoal(val period: GoalPeriod, val value: Int, val repeatCount: Int = 1)

data class AddictionHabit(
    val name: String,
    val startDate: LocalDate,
    val useLog: MutableMap<LocalDate, Int> = mutableMapOf(),
    val usagePlan: List<AbstinenceGoal> = emptyList(),
    val dailyNotes: MutableMap<LocalDate, String> = mutableMapOf()
) {
    fun logUse(date: LocalDate = LocalDate.now()) { useLog[date] = (useLog[date] ?: 0) + 1 }
    fun addNote(date: LocalDate, note: String) { dailyNotes[date] = note }
    fun getPeriodIndex(current: LocalDate, goal: AbstinenceGoal): Int =
        when (goal.period) {
            GoalPeriod.WEEKLY -> ChronoUnit.WEEKS.between(startDate, current).toInt()
            GoalPeriod.MONTHLY -> ChronoUnit.MONTHS.between(startDate.withDayOfMonth(1), current.withDayOfMonth(1)).toInt()
        }
    fun getCurrentGoal(current: LocalDate): AbstinenceGoal? {
        var index = 0
        for (goal in usagePlan) {
            val repeat = goal.repeatCount
            if (getPeriodIndex(current, goal) < index + repeat) return goal
            index += repeat
        }
        return usagePlan.lastOrNull()
    }
    fun countRestDaysInPeriod(current: LocalDate, goal: AbstinenceGoal): Int {
        val start = when (goal.period) {
            GoalPeriod.WEEKLY -> current.with(DayOfWeek.MONDAY)
            GoalPeriod.MONTHLY -> current.withDayOfMonth(1)
        }
        val end = when (goal.period) {
            GoalPeriod.WEEKLY -> start.plusDays(6)
            GoalPeriod.MONTHLY -> start.withDayOfMonth(start.lengthOfMonth())
        }
        return (0L..ChronoUnit.DAYS.between(start, end)).count { i ->
            val date = start.plusDays(i)
            (useLog[date] ?: 0) == 0
        }
    }
    fun isGoalMet(current: LocalDate): Boolean {
        val goal = getCurrentGoal(current) ?: return false
        return countRestDaysInPeriod(current, goal) >= goal.value
    }
    fun getDailyUseSummary(): List<Pair<LocalDate, Int>> = useLog.entries.sortedBy { it.key }.map { it.toPair() }
}

// --- Addiction Manager ---
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
}

// --- CLI Functions ---
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
    print("New name (leave blank to keep '\${habit.name}'): ")
    val newName = readLine()?.takeIf { it.isNotBlank() }

    println("Select new recurrence type or leave blank:")
    RecurrenceType.values().forEachIndexed { i, type -> println("${i + 1}. $type") }
    print("New recurrence type (1-\${RecurrenceType.values().size}): ")
    val recInput = readLine()?.toIntOrNull()
    val newRecurrence = recInput?.let { RecurrenceType.values().getOrNull(it - 1) }

    print("New preferred time (HH:mm) or leave blank to keep '\${habit.preferredTime}'): ")
    val timeInput = readLine()?.takeIf { it.isNotBlank() }
    val newPreferredTime = try {
        timeInput?.let { LocalTime.parse(it) }
    } catch (e: Exception) {
        null
    }

    print("New estimated time in minutes or leave blank to keep '\${habit.estimatedMinutes}'): ")
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
