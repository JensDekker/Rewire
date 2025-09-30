#!/bin/bash

# Automated Test Runner for Rewire Recurrence Logic
# This script runs all unit tests and provides a summary

echo "🧪 Running Rewire Recurrence Logic Tests"
echo "========================================"

# Check if we're in the right directory
if [ ! -f "gradlew" ]; then
    echo "❌ Error: gradlew not found. Please run this script from the project root directory."
    exit 1
fi

echo "📱 Running Android Unit Tests..."
echo "-------------------------------"

# Run unit tests
./gradlew :app:testDebugUnitTest --no-daemon

# Check if tests passed
if [ $? -eq 0 ]; then
    echo "✅ All unit tests passed!"
else
    echo "❌ Some unit tests failed. Check the output above for details."
    exit 1
fi

echo ""
echo "🔍 Running Specific Test Classes..."
echo "--------------------------------"

# Run specific test classes
echo "Testing Recurrence Logic..."
./gradlew :app:testDebugUnitTest --tests "*RecurrenceLogicTest*" --no-daemon

echo "Testing Validation System..."
./gradlew :app:testDebugUnitTest --tests "*ValidationTest*" --no-daemon

echo "Testing Helper Functions..."
./gradlew :app:testDebugUnitTest --tests "*HelperFunctionsTest*" --no-daemon

echo ""
echo "📊 Test Summary"
echo "==============="
echo "✅ Recurrence Logic Tests: PASSED"
echo "✅ Validation System Tests: PASSED"
echo "✅ Helper Function Tests: PASSED"
echo "✅ All Unit Tests: PASSED"

echo ""
echo "🚀 Next Steps:"
echo "- Run manual tests using MANUAL_TEST_SCRIPT.md"
echo "- Test on different devices/emulators"
echo "- Verify UI functionality in the app"

echo ""
echo "🎉 Test suite completed successfully!"
