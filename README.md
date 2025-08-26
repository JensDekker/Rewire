# Rewire Habit & Addiction Tracker

Rewire is a Kotlin-based habit and addiction tracker, now available as both a command-line application and a modern Android app. The project features flexible recurrence models, robust data persistence, and a modular architecture.

## Project Structure

- **CLI**: Command-line interface for habit/addiction tracking (see `cli/`)
- **Android App**: Full-featured mobile app (see `app/`)
  - **UI Components**: Composables for cards, modals, screens (`app/src/main/kotlin/ui/components/`, `ui/screens/`)
  - **Navigation**: Jetpack Compose navigation (`ui/navigation/`)
  - **Theming**: Custom colors, shapes, typography (`ui/theme/`)
  - **Persistence**: Room database entities, DAOs, repositories (`db/entity/`, `db/dao/`, `repository/`)
  - **Managers**: Business logic for habits and addictions (`manager/`)

## Android App Features

- Add, edit, delete, and view habits/addictions
- Mark habits as completed
- Log addiction usage events
- Add, edit, delete notes for habits/addictions
- Flexible usage plans with recurrence (daily, weekly, monthly, etc.)
- Data persists via Room database
- Modern UI with Jetpack Compose

## Building & Running

### CLI
- Build and run with Gradle:
  ```sh
  ./gradlew run
  ```
  Or on Windows:
  ```sh
  gradlew.bat run
  ```

### Android App
- Open the project in Android Studio
- Build and run on an emulator or device

## Manual Testing
- See `cli/src/main/resources/cli_exact_test_script.txt` for CLI test script
- For Android, use the app UI to test all flows

## Contributing
Pull requests and suggestions are welcome!

---

*This README reflects the current status: both CLI and Android app are supported, with a modular, scalable codebase.*
