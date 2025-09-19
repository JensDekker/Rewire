package com.example.rewire.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.PaddingValues

/**
 * Centralized shape and spacing definitions for consistent UI styling across the app
 */
object AppShapes {
    /**
     * Standard card shape used for habit cards, add buttons, and other main UI elements
     * - 16dp corner radius for modern, friendly appearance
     */
    val cardShape = RoundedCornerShape(16.dp)
    
    /**
     * Small card shape for compact UI elements
     * - 8dp corner radius for smaller components
     */
    val smallCardShape = RoundedCornerShape(8.dp)
    
    /**
     * Large card shape for prominent UI elements
     * - 24dp corner radius for modal dialogs and important cards
     */
    val largeCardShape = RoundedCornerShape(24.dp)
    
    /**
     * Button shape for interactive elements
     * - 12dp corner radius for buttons and interactive components
     */
    val buttonShape = RoundedCornerShape(12.dp)
    
    /**
     * Input field shape for text inputs and form elements
     * - 8dp corner radius for input fields
     */
    val inputShape = RoundedCornerShape(8.dp)
}

/**
 * Centralized padding and spacing definitions for consistent UI layout across the app
 */
object AppSpacing {
    /**
     * Standard card padding used for habit cards, add buttons, and other main UI elements
     * - 11dp horizontal, 8dp vertical for consistent card spacing
     */
    val cardPadding = PaddingValues(horizontal = 11.dp, vertical = 8.dp)
    
    /**
     * Modal padding for dialog and modal content
     * - 16dp all around for comfortable modal spacing
     */
    val modalPadding = PaddingValues(16.dp)
    
    /**
     * Small padding for compact elements
     * - 8dp all around for tight spacing
     */
    val smallPadding = PaddingValues(8.dp)
    
    /**
     * Large padding for prominent elements
     * - 24dp all around for spacious layouts
     */
    val largePadding = PaddingValues(24.dp)
    
    /**
     * Standard spacing between elements
     * - 16dp for consistent element separation
     */
    val standardSpacing = 16.dp
    
    /**
     * Small spacing for tight layouts
     * - 8dp for compact element separation
     */
    val smallSpacing = 8.dp
    
    /**
     * Large spacing for spacious layouts
     * - 24dp for generous element separation
     */
    val largeSpacing = 24.dp
    
    /**
     * Standard row height for consistent component sizing
     * - 64dp for habit cards and similar components
     */
    val standardRowHeight = 64.dp
}
