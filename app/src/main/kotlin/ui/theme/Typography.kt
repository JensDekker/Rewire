package com.example.rewire.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Centralized typography definitions for consistent text styling across the app
 */
object AppTypography {
    // Base font family - using system default
    private val fontFamily = FontFamily.Default
    
    // Material Typography with custom overrides
    val materialTypography = Typography(
        h1 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 96.sp,
            letterSpacing = (-1.5).sp
        ),
        h2 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 60.sp,
            letterSpacing = (-0.5).sp
        ),
        h3 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 48.sp,
            letterSpacing = 0.sp
        ),
        h4 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 34.sp,
            letterSpacing = 0.25.sp
        ),
        h5 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            letterSpacing = 0.sp
        ),
        h6 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 0.15.sp
        ),
        subtitle1 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.15.sp
        ),
        subtitle2 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 0.1.sp
        ),
        body1 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        ),
        body2 = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.25.sp
        ),
        button = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            letterSpacing = 1.25.sp
        ),
        caption = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp
        ),
        overline = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    )
    
    // Custom typography styles for specific use cases
    object Custom {
        // Habit name in cards and modals
        val habitName = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp,
            letterSpacing = 0.sp
        )
        
        // Large habit name in modal header
        val habitNameLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp,
            letterSpacing = 0.sp
        )
        
        // Statistics numbers (completion streak, percentage, etc.)
        val statisticsNumber = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            letterSpacing = 0.sp
        )
        
        // Statistics labels
        val statisticsLabel = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.sp
        )
        
        // Recurrence section text
        val recurrenceText = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp,
            letterSpacing = 0.sp
        )
        
        // Day of week circle text
        val dayCircleText = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 17.sp,
            letterSpacing = 0.sp
        )
        
        // Time display text
        val timeText = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Light,
            fontSize = 32.sp,
            letterSpacing = 0.sp
        )
        
        // Time labels
        val timeLabel = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            letterSpacing = 0.sp
        )
        
        // Note field label
        val noteLabel = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            letterSpacing = 0.sp
        )
    }
}
