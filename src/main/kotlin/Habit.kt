import java.time.*

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
