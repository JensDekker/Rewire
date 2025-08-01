# Rewire

Rewire is a Kotlin-based habit and addiction tracker designed to help users build positive habits and manage addictive behaviors. The project is structured for maintainability and extensibility, with clear separation of concerns and modular code organization.

## Features

- **Habit Tracking:**
  - Create, edit, and delete habits
  - Add notes and completions to habits
  - List and manage all habits

- **Addiction Management:**
  - Create, edit, and delete addiction habits
  - Define and manage usage plans (formerly abstinence goals)
  - Track daily usage and progress toward goals
  - Print addiction statistics and usage plans

- **Usage Plan Operations:**
  - Add, update, delete, and reorder usage plan items
  - Replace or clear entire usage plans

- **CLI Functions:**
  - Command-line interface for managing habits and addictions

## File Structure

- `src/main/kotlin/Habit.kt` — Habit data class and note/completion management
- `src/main/kotlin/HabitManager.kt` — CRUD operations for habits
- `src/main/kotlin/AddictionHabit.kt` — AddictionHabit data class and goal logic
- `src/main/kotlin/AddictionManager.kt` — CRUD operations for addiction habits and usage plans
- `src/main/kotlin/AbstinenceGoal.kt` — AbstinenceGoal data class and GoalPeriod enum
- `src/main/kotlin/RecurrenceType.kt` — RecurrenceType enum
- `src/main/kotlin/CliFunctions.kt` — CLI functions for user interaction
- `src/main/kotlin/Main.kt` — Entry point (template)

## Getting Started

1. **Clone the repository:**
   ```
   git clone https://github.com/JensDekker/Rewire.git
   ```
2. **Open in Android Studio or your preferred Kotlin IDE.**
3. **Build and run the project.**

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request with your changes.

## License

This project is licensed under the Creative Commons Attribution-NonCommercial 4.0 International (CC BY-NC 4.0) License.

**Summary:**
- You may use, share, and adapt the code for non-commercial purposes only.
- Commercial use (selling, using in paid products/services) is not allowed.
- You must provide appropriate credit to the original author.
- For full license details, see: https://creativecommons.org/licenses/by-nc/4.0/

## Author

Jens Dekker
