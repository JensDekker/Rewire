package com.example.rewire

import com.example.rewire.ui.screens.getWeekLabel
import com.example.rewire.ui.screens.getQuarterDescription
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for helper functions
 */
class HelperFunctionsTest {

    @Test
    fun `getWeekLabel - Returns correct labels for weeks 1-4`() {
        assertEquals("1st", getWeekLabel(1))
        assertEquals("2nd", getWeekLabel(2))
        assertEquals("3rd", getWeekLabel(3))
        assertEquals("4th", getWeekLabel(4))
    }

    @Test
    fun `getWeekLabel - Returns correct label for week 5 and beyond`() {
        assertEquals("5th", getWeekLabel(5))
        assertEquals("10th", getWeekLabel(10))
    }

    @Test
    fun `getQuarterDescription - Returns correct descriptions for month offsets`() {
        assertEquals("January, April, July, October", getQuarterDescription(0))
        assertEquals("February, May, August, November", getQuarterDescription(1))
        assertEquals("March, June, September, December", getQuarterDescription(2))
    }

    @Test
    fun `getQuarterDescription - Returns Unknown for invalid offset`() {
        assertEquals("Unknown", getQuarterDescription(3))
        assertEquals("Unknown", getQuarterDescription(-1))
    }
}
