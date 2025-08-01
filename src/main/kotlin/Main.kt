fun main() {
    val habitManager = HabitManager()
    habitManager.addHabit(Habit(name = "Drink water"))

    cliEditHabit(habitManager)
}
