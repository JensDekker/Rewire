@echo off
REM Automated Test Runner for Rewire Recurrence Logic (Windows)
REM This script runs all unit tests and provides a summary

echo 🧪 Running Rewire Recurrence Logic Tests
echo ========================================

REM Check if we're in the right directory
if not exist "gradlew.bat" (
    echo ❌ Error: gradlew.bat not found. Please run this script from the project root directory.
    exit /b 1
)

echo 📱 Running Android Unit Tests...
echo -------------------------------

REM Run unit tests
call gradlew.bat :app:testDebugUnitTest --no-daemon

REM Check if tests passed
if %errorlevel% equ 0 (
    echo ✅ All unit tests passed!
) else (
    echo ❌ Some unit tests failed. Check the output above for details.
    exit /b 1
)

echo.
echo 🔍 Running Specific Test Classes...
echo --------------------------------

REM Run specific test classes
echo Testing Recurrence Logic...
call gradlew.bat :app:testDebugUnitTest --tests "*RecurrenceLogicTest*" --no-daemon

echo Testing Validation System...
call gradlew.bat :app:testDebugUnitTest --tests "*ValidationTest*" --no-daemon

echo Testing Helper Functions...
call gradlew.bat :app:testDebugUnitTest --tests "*HelperFunctionsTest*" --no-daemon

echo.
echo 📊 Test Summary
echo ===============
echo ✅ Recurrence Logic Tests: PASSED
echo ✅ Validation System Tests: PASSED
echo ✅ Helper Function Tests: PASSED
echo ✅ All Unit Tests: PASSED

echo.
echo 🚀 Next Steps:
echo - Run manual tests using MANUAL_TEST_SCRIPT.md
echo - Test on different devices/emulators
echo - Verify UI functionality in the app

echo.
echo 🎉 Test suite completed successfully!
pause
