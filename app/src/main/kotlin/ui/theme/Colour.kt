package com.example.rewire.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Centralized color definitions for consistent UI theming across the app
 */
object AppColors {
    // Primary Colors
    val primary = Color(0xFF6200EE)
    val primaryVariant = Color(0xFF3700B3)
    val onPrimary = Color(0xFFFFFFFF)
    
    // Secondary Colors
    val secondary = Color(0xFF03DAC6)
    val secondaryVariant = Color(0xFF018786)
    val onSecondary = Color(0xFF000000)
    
    // Surface Colors
    val surface = Color(0xFFFFFFFF)
    val onSurface = Color(0xFF000000)
    val surfaceVariant = Color(0xFFF5F5F5)
    
    // Background Colors
    val background = Color(0xFFFFFFFF)
    val onBackground = Color(0xFF000000)
    
    // Error Colors
    val error = Color(0xFFB00020)
    val onError = Color(0xFFFFFFFF)
    
    // Day of Week Circle Colors
    val dayCircleSelected = primary
    val dayCircleUnselected = Color.Transparent
    val dayCircleBorder = onSurface.copy(alpha = 0.3f)
    val dayCircleTextSelected = onPrimary
    val dayCircleTextUnselected = onSurface
    
    // Text Colors
    val textPrimary = onSurface
    val textSecondary = onSurface.copy(alpha = 0.6f)
    val textAccent = primary
    
    // Border Colors
    val borderLight = onSurface.copy(alpha = 0.2f)
    val borderMedium = onSurface.copy(alpha = 0.3f)
    val borderStrong = onSurface.copy(alpha = 0.5f)
}
