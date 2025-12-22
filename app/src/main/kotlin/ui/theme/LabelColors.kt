package com.example.rewire.ui.theme

/**
 * Centralized color definitions for labels
 */
object LabelColors {
    val DEFAULT_COLORS = listOf(
        "#F44336", // Red
        "#E91E63", // Pink
        "#9C27B0", // Purple
        "#673AB7", // Deep Purple
        "#3F51B5", // Indigo
        "#2196F3", // Blue
        "#03A9F4", // Light Blue
        "#00BCD4", // Cyan
        "#009688", // Teal
        "#4CAF50", // Green
        "#8BC34A", // Light Green
        "#CDDC39", // Lime
        "#FFEB3B", // Yellow
        "#FFC107", // Amber
        "#FF9800", // Orange
        "#FF5722", // Deep Orange
        "#795548", // Brown
        "#9E9E9E", // Grey
        "#607D8B"  // Blue Grey
    )
    
    fun getDefaultColor(index: Int): String {
        return DEFAULT_COLORS[index % DEFAULT_COLORS.size]
    }
    
    /**
     * Get the next available default color that isn't already used.
     * This helps avoid color collisions when creating new labels.
     * 
     * @param usedColors List of colors already in use
     * @return A color from DEFAULT_COLORS that isn't in usedColors, or the first default color if all are used
     */
    fun getNextAvailableColor(usedColors: List<String>): String {
        val availableColor = DEFAULT_COLORS.firstOrNull { it !in usedColors }
        return availableColor ?: DEFAULT_COLORS.first()
    }
    
    /**
     * Get a default color for a new label based on existing label count.
     * Cycles through colors to provide variety.
     * 
     * @param existingLabelCount Number of existing labels (used to determine color index)
     * @return A color from DEFAULT_COLORS
     */
    fun getDefaultColorForNewLabel(existingLabelCount: Int): String {
        return getDefaultColor(existingLabelCount)
    }
}

