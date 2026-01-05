# Rewire Habit & Addiction Tracker

Rewire is a Kotlin-based habit and addiction tracker, now available as both a command-line application and a modern Android app. The project features **comprehensive recurrence logic**, **label/tag system with color coding**, robust data persistence with Room database, and a modular architecture with state-of-the-art UI components built with Jetpack Compose.

## Project Structure

- **CLI**: Command-line interface for habit/addiction tracking (see `cli/`)
- **Android App**: Full-featured mobile app (see `app/`)
  - **UI Components**: Composables for cards, modals, screens (`app/src/main/kotlin/ui/components/`, `ui/screens/`)
  - **Navigation**: Jetpack Compose navigation (`ui/navigation/`)
  - **Theming**: Custom colors, shapes, typography (`ui/theme/`)
  - **Persistence**: Room database entities, DAOs, repositories (`db/entity/`, `db/dao/`, `repository/`)
  - **Managers**: Business logic for habits and addictions (`manager/`)

## Android App Features

### Core Functionality
- **Add, Edit, Delete Habits**: Full CRUD operations with intuitive UI
- **Habit Completion Tracking**: Mark habits as completed with persistence
- **Note Management**: Add, edit, delete notes for habits/addictions
- **Data Persistence**: Robust Room database integration with automatic migrations
- **Label System**: Color-coded labels/tags for organizing habits (95% complete)
  - Create, edit, and delete custom labels
  - Assign multiple labels to habits
  - Filter habits by labels
  - Custom color selection with pastel/muted color palette
  - Visual label indicators on habit cards

### 🎯 Advanced Recurrence System
- **7 Recurrence Types**: Daily, Weekly, Custom Weekly, Monthly (by date/weekday), Quarterly (by date/weekday)
- **Interactive UI Components**: 
  - Day selection grids for weekly/monthly patterns
  - Quarter selection with month groupings
  - Visual feedback and validation
- **Smart Validation**: Real-time validation with user-friendly error messages
- **Natural Language Display**: Human-readable recurrence descriptions

### UI/UX Features
- **Material 3 Design**: Modern, consistent design system
- **State-based Navigation**: Seamless flow between screens
- **Time Picker Integration**: Built-in Material 3 time selection
- **Responsive Layouts**: Adapts to different screen sizes
- **Visual Feedback**: Clear selection states and interactive elements
- **Label Management Screen**: Dedicated interface for managing labels
- **Color-coded Habit Cards**: Visual organization with label-based background colors

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
- **Prerequisites**: Android Studio with Kotlin support
- **Build Time**: ~20-30 seconds (optimized with parallel builds)
- **Dependencies**: Material 3, Room database, Jetpack Compose
- **Run**: Open in Android Studio → Build → Run on emulator/device
- **Installation**: See [`docs/reference/INSTALL_ON_PHONE.md`](docs/reference/INSTALL_ON_PHONE.md) for detailed installation instructions
  - Install via USB/ADB
  - Manual APK transfer and installation
  - Android Studio direct deployment

### Build Optimization
- **Parallel Builds**: Enabled for faster compilation
- **Material 3**: Latest design system integration
- **Kotlin Compiler**: Optimized for performance

## Technical Implementation

### Recurrence Logic Architecture
- **Type-Safe Design**: Sealed class hierarchy for recurrence patterns
- **Validation System**: Comprehensive parameter validation with user feedback
- **State Management**: Reactive UI with Compose state management
- **Data Flow**: Clean separation between UI, business logic, and persistence

### Key Components
- **AddEditHabitScreen**: Main habit creation/editing interface
- **Recurrence Configuration**: Interactive UI for all recurrence types
- **Validation Engine**: Real-time validation with error prevention
- **Time Picker**: Material 3 time selection component
- **State-based Navigation**: Seamless screen transitions

### Supported Recurrence Patterns
1. **Daily**: Every day
2. **Weekly**: Specific days of the week
3. **Custom Weekly**: Multiple selected days
4. **Monthly by Date**: Specific day of month (e.g., 15th)
5. **Monthly by Weekday**: Specific weekday of month (e.g., 3rd Friday)
6. **Quarterly by Date**: Specific day every 3 months
7. **Quarterly by Weekday**: Specific weekday every 3 months

## Manual Testing

### CLI Testing
- See `cli/src/main/resources/cli_exact_test_script.txt` for CLI test script

### Android App Testing
- **Habit Creation**: Test all recurrence types and validation
- **Time Selection**: Verify Material 3 time picker functionality
- **Data Persistence**: Confirm habits save and load correctly
- **UI Interactions**: Test all interactive elements and navigation
- **Validation**: Verify error handling and user guidance
- **Label Management**: Test label creation, assignment, and filtering
- **Testing Guides**: See [`docs/testing/`](docs/testing/) for comprehensive test procedures

### Test Coverage
- ✅ All recurrence types compile and function
- ✅ Validation system prevents invalid configurations
- ✅ UI components render correctly
- ✅ State management works properly
- ✅ Data persistence functions correctly

## Documentation

The project includes comprehensive documentation organized in the `docs/` directory:

### Documentation Structure

- **`docs/reference/`** - Operational guides and how-to documentation
  - [`INSTALL_ON_PHONE.md`](docs/reference/INSTALL_ON_PHONE.md) - Installation guide for side-loading the app
  - [`UPDATE_VERSION.md`](docs/reference/UPDATE_VERSION.md) - Version management and update procedures

- **`docs/implementation/`** - Feature implementation plans and technical specifications
  - [`LABELS_IMPLEMENTATION_PLAN.md`](docs/implementation/LABELS_IMPLEMENTATION_PLAN.md) - Labels feature implementation (95% complete)
  - [`NOTIFICATIONS_IMPLEMENTATION_PLAN.md`](docs/implementation/NOTIFICATIONS_IMPLEMENTATION_PLAN.md) - Planned notification system
  - [`UTILITIES_MENU_IMPLEMENTATION_PLAN.md`](docs/implementation/UTILITIES_MENU_IMPLEMENTATION_PLAN.md) - Planned utilities menu feature

- **`docs/testing/`** - Testing procedures and quality assurance guides
  - [`MANUAL_TEST_SCRIPT.md`](docs/testing/MANUAL_TEST_SCRIPT.md) - Comprehensive manual testing procedures
  - [`NAVIGATION_TEST_GUIDE.md`](docs/testing/NAVIGATION_TEST_GUIDE.md) - Navigation flow testing guide

- **`docs/design/`** - Design exploration documents and concept notes
  - [`HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md`](docs/design/HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md) - UI design concepts

- **`docs/00_SUMMARY.md`** - Overview of all documentation and implementation status

### Additional Documentation

- **Recurrence Logic**: See `RECURRENCE_LOGIC_DOCUMENTATION.md` for detailed implementation docs
- **Code Structure**: Well-documented with inline comments
- **API Design**: Type-safe, extensible architecture

## Current Status

### Completed Features ✅
- Core habit CRUD operations
- Advanced recurrence system (7 types)
- Room database persistence with migrations
- Label system (95% - one UI refinement pending)
- Note management
- Habit completion tracking
- Material 3 UI components
- Comprehensive testing documentation

### In Progress 🚧
- Label system UI refinements (Step 6.10 pending)

### Planned Features 📋
- Notification system (see [`docs/implementation/NOTIFICATIONS_IMPLEMENTATION_PLAN.md`](docs/implementation/NOTIFICATIONS_IMPLEMENTATION_PLAN.md))
- Utilities menu (see [`docs/implementation/UTILITIES_MENU_IMPLEMENTATION_PLAN.md`](docs/implementation/UTILITIES_MENU_IMPLEMENTATION_PLAN.md))
- Enhanced home screen design (see [`docs/design/HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md`](docs/design/HABIT_HOME_SCREEN_BACKGROUND_DESIGN.md))

## Contributing

We welcome contributions! Areas of focus:
- **New Recurrence Types**: Extending the recurrence system
- **UI/UX Improvements**: Enhancing user experience
- **Performance Optimization**: Improving app performance
- **Testing**: Adding comprehensive test coverage
- **Documentation**: Improving code documentation
- **Feature Implementation**: See [`docs/implementation/`](docs/implementation/) for planned features

### Development Setup
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Installation & Distribution

### Installing on Your Phone

The app can be installed directly on Android devices without using the Play Store. See the comprehensive guide:

📱 **[Installation Guide](docs/reference/INSTALL_ON_PHONE.md)**

This guide covers:
- USB/ADB installation
- Manual APK transfer
- Android Studio deployment
- Troubleshooting common issues
- **Updating the app** with data preservation

### Building APKs

```powershell
# Debug APK
.\gradlew.bat assembleDebug

# Release APK (requires signing configuration)
.\gradlew.bat assembleRelease
```

APK files are located at:
- Debug: `app/build/outputs/apk/debug/app-debug.apk`
- Release: `app/build/outputs/apk/release/app-release.apk`

### Version Management

When releasing updates, see [`docs/reference/UPDATE_VERSION.md`](docs/reference/UPDATE_VERSION.md) for:
- Version number management
- Database migration procedures
- Data preservation during updates

---

*This README reflects the current status: both CLI and Android app are supported, with a comprehensive recurrence system, label organization, modern UI components, robust data persistence, and a modular, scalable codebase.*
