
# Rewire Habit & Addiction Tracker CLI

Rewire is a Kotlin command-line application for tracking habits and managing addiction usage plans with flexible recurrence and permitted use models.

## Features

- **Habits**
  - Add, edit, delete, and list habits
  - Mark habits as completed for today
  - Add, edit, and delete notes for each habit (with date)
  - View all days with notes for a habit

- **Addictions**
  - Add, edit, delete, and list addictions
  - Log usage events (date and optional time)
  - Add, edit, and delete notes for each addiction (with date)
  - Flexible usage plans:
    - Set permitted uses per recurrence (e.g., 7 uses per WEEKLY, 2 per WEEKENDS, etc.)
    - Recurrence options: DAILY, WEEKLY, MONTHLY, QUARTERLY, WEEKDAYS, WEEKENDS, CUSTOM_WEEKLY
    - Multiple plan items per addiction supported
    - Edit, delete, and clear usage plans
    - Usage plan enforcement and stats

- **Menus & Navigation**
  - Main menu: Habits, Addictions, Exit
  - Context-persistent selection for editing and logging
  - Robust error handling for invalid input

- **Data Persistence**
  - (If supported) Data persists between CLI sessions (habits, addictions, notes, usage plans)

- **Manual Test Script**
  - See `cli/src/main/resources/cli_exact_test_script.txt` for a step-by-step test script and expected outputs

## Usage

1. **Build and Run**
   - Use Gradle to build and run the CLI:
     ```sh
     ./gradlew run
     ```
     Or on Windows:
     ```sh
     gradlew.bat run
     ```

2. **Follow the Menus**
   - Use the number keys to navigate menus and perform actions
   - Enter blank, out-of-range, or non-numeric input to test error handling

3. **Add Habits and Addictions**
   - Add new habits and addictions from their respective menus
   - For addictions, set up usage plans with permitted uses and recurrence

4. **Log Usage and Notes**
   - Log usage for addictions (date and optional time)
   - Add notes for both habits and addictions (date and text)

5. **Edit and Manage**
   - Edit names, notes, and usage plans
   - Delete or clear items as needed

6. **Persistence**
   - (If supported) Data will persist between CLI sessions

## Recurrence Types

- `DAILY`: Every day
- `WEEKLY`: Every week
- `MONTHLY`: Every month
- `QUARTERLY`: Every three months
- `WEEKDAYS`: Monday to Friday
- `WEEKENDS`: Saturday and Sunday
- `CUSTOM_WEEKLY`: Custom weekly recurrence (if implemented)

## Example Usage Plan

- "7 use(s) permitted per WEEKLY, x1" (7 uses allowed per week, for 1 week)
- Multiple plan items can be added for different recurrences

## Testing

- See `cli/src/main/resources/cli_exact_test_script.txt` for a comprehensive manual test script
- The script covers all menu flows, error handling, and expected outputs

## File Structure

- `src/main/kotlin/Habit.kt` — Habit data class and note/completion management
- `src/main/kotlin/HabitManager.kt` — CRUD operations for habits
- `src/main/kotlin/AddictionHabit.kt` — AddictionHabit data class and recurrence/usage logic
- `src/main/kotlin/AddictionManager.kt` — CRUD operations for addiction habits and usage plans
- `src/main/kotlin/AbstinenceGoal.kt` — Usage plan data class (recurrence, permitted uses, repeat count)
- `src/main/kotlin/RecurrenceType.kt` — RecurrenceType enum
- `src/main/kotlin/CliFunctions.kt` — CLI functions for user interaction
- `src/main/kotlin/Main.kt` — Entry point

## Contributing

Pull requests and suggestions are welcome!

## Getting Started

1. **Clone the repository:**
   ```
   git clone https://github.com/JensDekker/Rewire.git
   ```
2. **Open in Android Studio or your preferred Kotlin IDE.**
3. **Build and run the project.**
## Manual CLI Testing Procedure

The following steps ensure all CLI features and error handling are working as intended:

### 1. General Navigation & Error Handling
- Start the CLI and verify main menu appears.
- Try invalid inputs (blank, out-of-range numbers, non-numeric) at each menu.
- Test "Back" and "Exit" options in all menus.

### 2. Habits
- List habits (should be empty initially).
- Add a new habit.
- List habits (should show the new habit).
- Select the habit:
  - Edit habit details.
  - Show habit details.
  - Complete habit.
  - Manage habit notes:
    - List notes (should be empty).
    - Add a note.
    - List notes (should show the new note).
    - Edit the note.
    - Delete the note.
    - List notes (should be empty again).
  - Delete the habit.
- List habits (should be empty).

### 3. Addictions
- List addictions (should be empty initially).
- Add a new addiction.
- List addictions (should show the new addiction).
- Select the addiction:
  - View addiction details.
  - Edit addiction details.
  - Log usage for the addiction.
  - Manage addiction notes:
    - List notes (should be empty).
    - Add a note.
    - List notes (should show the new note).
    - Edit the note.
    - Delete the note.
    - List notes (should be empty again).
  - Manage usage plan:
    - List plan items (should be empty).
    - Add a plan item.
    - List plan items (should show the new item).
    - Edit the plan item.
    - Delete the plan item.
    - List plan items (should be empty again).
    - Add multiple plan items.
    - Clear the usage plan.
    - List plan items (should be empty).
  - Delete the addiction.
- List addictions (should be empty).

### 4. Data Persistence (if supported)
- Exit the CLI and restart.
- Verify that habits/addictions/notes/usage plans persist or reset as expected.

### 5. Final Checks
- Confirm all error messages display for invalid actions.
- Confirm all menu navigation works as expected.

## Exact CLI Test Script

For step-by-step verification, follow the detailed script below:

1. **Start CLI**
   - Verify main menu appears.
   - Enter blank, out-of-range, and non-numeric input at main menu → confirm error message.
   - Select "Habits".
2. **Habits Menu**
   - Enter blank, out-of-range, and non-numeric input → confirm error message.
   - List habits (should be empty).
   - Add habit: "Exercise".
   - Add habit: "Read".
   - List habits (should show both).
   - Select "Select Habit", choose "Exercise":
     - Edit habit: Change name to "Morning Exercise".
     - Show habit details.
     - Complete habit.
     - Habit Notes:
       - Enter blank, out-of-range, non-numeric input → confirm error message.
       - List notes (should be empty).
       - Add note: Date = today, Note = "Started running."
       - List notes (should show the new note).
       - Edit note: Change to "Ran 5km."
       - Delete note.
       - List notes (should be empty).
     - Back to habit menu, delete habit.
   - List habits (should show only "Read").
   - Select "Select Habit", choose "Read":
     - Add note: Date = today, Note = "Read 20 pages."
     - List notes.
     - Delete habit.
   - List habits (should be empty).
   - Back to main menu.
3. **Addictions Menu**
   - Enter blank, out-of-range, non-numeric input → confirm error message.
   - List addictions (should be empty).
   - Add addiction: "Sugar".
   - Add addiction: "Caffeine".
   - List addictions (should show both).
   - Select "Select Addiction", choose "Sugar":
     - View details.
     - Edit addiction: Change name to "No Sugar".
     - Log usage: Date = today.
     - Addiction Notes:
       - Enter blank, out-of-range, non-numeric input → confirm error message.
       - Add note: Date = today, Note = "Craving after lunch."
       - List notes.
       - Edit note: Change to "Resisted craving."
       - Delete note.
       - List notes (should be empty).
     - Usage Plan:
       - Enter blank, out-of-range, non-numeric input → confirm error message.
       - Add plan item: Description = "No sugar for a week", Period = "WEEKLY", Value = 7, Repeat = 1.
       - List plan items.
       - Edit plan item: Change description to "No sugar for two weeks", Value = 14.
       - Delete plan item.
       - List plan items (should be empty).
       - Add multiple plan items.
       - Clear usage plan.
       - List plan items (should be empty).
     - Back to addiction menu, delete addiction.
   - List addictions (should show only "Caffeine").
   - Select "Select Addiction", choose "Caffeine":
     - Add note: Date = today, Note = "Had coffee in the morning."
     - List notes.
     - Delete addiction.
   - List addictions (should be empty).
   - Back to main menu.
4. **Data Persistence**
   - Exit the CLI and restart.
   - Verify that habits/addictions/notes/usage plans persist or reset as expected.
5. **Final Checks**
   - Confirm all error messages display for invalid actions.
   - Confirm all menu navigation works as expected.

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
