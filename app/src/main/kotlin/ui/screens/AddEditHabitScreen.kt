package com.example.rewire.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography
import com.example.rewire.ui.components.DeleteButton
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyConfigurationSection(
    selectedDaysOfWeek: Set<DayOfWeek>,
    onDaysOfWeekChange: (Set<DayOfWeek>) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select Days:",
            style = AppTypography.Custom.recurrenceText,
            color = AppColors.textAccent,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { day ->
                val isSelected = selectedDaysOfWeek.contains(day)
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val newSelection = if (isSelected) {
                                selectedDaysOfWeek - day
                            } else {
                                selectedDaysOfWeek + day
                            }
                            onDaysOfWeekChange(newSelection)
                        },
                    shape = CircleShape,
                    color = if (isSelected) AppColors.primary else AppColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (isSelected) AppColors.primary else AppColors.borderMedium
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.name.take(1), // M, T, W, etc.
                            style = AppTypography.Custom.statisticsLabel,
                            color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary
                        )
                    }
                }
            }
        }
        
        // Show validation message
        val validationMessage = getValidationMessage("Weekly", selectedDaysOfWeek = selectedDaysOfWeek)
        if (validationMessage != null) {
            Text(
                text = validationMessage,
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
                color = AppColors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.smallSpacing),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun MonthlyConfigurationSection(
    monthlySubType: String,
    onMonthlySubTypeChange: (String) -> Unit,
    dayOfMonth: Int,
    onDayOfMonthChange: (Int) -> Unit,
    weekOfMonth: Int,
    onWeekOfMonthChange: (Int) -> Unit,
    selectedDayOfWeek: DayOfWeek,
    onSelectedDayOfWeekChange: (DayOfWeek) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Radio buttons for Monthly subtypes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = monthlySubType == "day",
                onClick = { onMonthlySubTypeChange("day") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Day of month",
                style = AppTypography.Custom.statisticsLabel
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = monthlySubType == "weekday",
                onClick = { onMonthlySubTypeChange("weekday") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Weekday of month",
                style = AppTypography.Custom.statisticsLabel
            )
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Specific configuration based on selected subtype
        if (monthlySubType == "day") {
            DayOfMonthSelector(
                dayOfMonth = dayOfMonth,
                onDayOfMonthChange = onDayOfMonthChange
            )
        } else {
            WeekdayOfMonthSelector(
                weekOfMonth = weekOfMonth,
                onWeekOfMonthChange = onWeekOfMonthChange,
                selectedDayOfWeek = selectedDayOfWeek,
                onSelectedDayOfWeekChange = onSelectedDayOfWeekChange
            )
        }
        
        // Show validation message
        val validationMessage = getValidationMessage(
            "Monthly", 
            monthlySubType, 
            dayOfMonth = dayOfMonth, 
            weekOfMonth = weekOfMonth, 
            selectedDayOfWeek = selectedDayOfWeek
        )
        if (validationMessage != null) {
            Text(
                text = validationMessage,
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
                color = AppColors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.smallSpacing),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun DayOfMonthSelector(
    dayOfMonth: Int,
    onDayOfMonthChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select Day:",
            style = AppTypography.Custom.recurrenceText,
            color = AppColors.textAccent,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Create day buttons (1-31)
            (1..31).chunked(7).forEach { week ->
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    week.forEach { day ->
                        val isSelected = dayOfMonth == day
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .padding(2.dp)
                                .clickable { onDayOfMonthChange(day) },
                            shape = CircleShape,
                            color = if (isSelected) AppColors.primary else AppColors.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, 
                                if (isSelected) AppColors.primary else AppColors.borderMedium
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
                                    color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeekdayOfMonthSelector(
    weekOfMonth: Int,
    onWeekOfMonthChange: (Int) -> Unit,
    selectedDayOfWeek: DayOfWeek,
    onSelectedDayOfWeekChange: (DayOfWeek) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Week selector (1st, 2nd, 3rd, 4th)
        Text(
            text = "Select Week:",
            style = AppTypography.Custom.recurrenceText,
            color = AppColors.textAccent,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (1..4).forEach { week ->
                val isSelected = weekOfMonth == week
                val weekLabel = when (week) {
                    1 -> "1st"
                    2 -> "2nd"
                    3 -> "3rd"
                    4 -> "4th"
                    else -> "${week}th"
                }
                
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onWeekOfMonthChange(week) },
                    shape = CircleShape,
                    color = if (isSelected) AppColors.primary else AppColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (isSelected) AppColors.primary else AppColors.borderMedium
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = weekLabel,
                            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 10.sp),
                            color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Day of week selector
        Text(
            text = "Select Day:",
            style = AppTypography.Custom.recurrenceText,
            color = AppColors.textAccent,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DayOfWeek.values().forEach { day ->
                val isSelected = selectedDayOfWeek == day
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onSelectedDayOfWeekChange(day) },
                    shape = CircleShape,
                    color = if (isSelected) AppColors.primary else AppColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (isSelected) AppColors.primary else AppColors.borderMedium
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day.name.take(3), // Mon, Tue, Wed, etc.
                            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 10.sp),
                            color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary
                        )
                    }
                }
            }
        }
        
        // Show current selection
        Text(
            text = "Every ${getWeekLabel(weekOfMonth)} ${selectedDayOfWeek.name.lowercase()}",
            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
            color = AppColors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.smallSpacing),
            textAlign = TextAlign.Center
        )
    }
}

private fun getWeekLabel(week: Int): String = when (week) {
    1 -> "1st"
    2 -> "2nd"
    3 -> "3rd"
    4 -> "4th"
    else -> "${week}th"
}

@Composable
fun QuarterlyConfigurationSection(
    quarterlySubType: String,
    onQuarterlySubTypeChange: (String) -> Unit,
    dayOfMonth: Int,
    onDayOfMonthChange: (Int) -> Unit,
    weekOfMonth: Int,
    onWeekOfMonthChange: (Int) -> Unit,
    selectedDayOfWeek: DayOfWeek,
    onSelectedDayOfWeekChange: (DayOfWeek) -> Unit,
    monthOffset: Int,
    onMonthOffsetChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Radio buttons for Quarterly subtypes
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = quarterlySubType == "day",
                onClick = { onQuarterlySubTypeChange("day") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Day of month",
                style = AppTypography.Custom.statisticsLabel
            )
        }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = quarterlySubType == "weekday",
                onClick = { onQuarterlySubTypeChange("weekday") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Weekday of month",
                style = AppTypography.Custom.statisticsLabel
            )
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Month offset selector (which quarter of the year)
        QuarterMonthSelector(
            monthOffset = monthOffset,
            onMonthOffsetChange = onMonthOffsetChange
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Specific configuration based on selected subtype
        if (quarterlySubType == "day") {
            DayOfMonthSelector(
                dayOfMonth = dayOfMonth,
                onDayOfMonthChange = onDayOfMonthChange
            )
        } else {
            WeekdayOfMonthSelector(
                weekOfMonth = weekOfMonth,
                onWeekOfMonthChange = onWeekOfMonthChange,
                selectedDayOfWeek = selectedDayOfWeek,
                onSelectedDayOfWeekChange = onSelectedDayOfWeekChange
            )
        }
        
        // Show validation message
        val validationMessage = getValidationMessage(
            "Quarterly", 
            quarterlySubType = quarterlySubType,
            dayOfMonth = dayOfMonth, 
            weekOfMonth = weekOfMonth, 
            selectedDayOfWeek = selectedDayOfWeek,
            monthOffset = monthOffset
        )
        if (validationMessage != null) {
            Text(
                text = validationMessage,
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
                color = AppColors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppSpacing.smallSpacing),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun QuarterMonthSelector(
    monthOffset: Int,
    onMonthOffsetChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Select Quarter Start:",
            style = AppTypography.Custom.recurrenceText,
            color = AppColors.textAccent,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            (0..2).forEach { offset ->
                val isSelected = monthOffset == offset
                
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .clickable { onMonthOffsetChange(offset) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AppColors.primary else AppColors.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, 
                        if (isSelected) AppColors.primary else AppColors.borderMedium
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Q${offset + 1}",
                            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 14.sp),
                            color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                        Text(
                            text = when (offset) {
                                0 -> "Jan/Apr/Jul/Oct"
                                1 -> "Feb/May/Aug/Nov"
                                2 -> "Mar/Jun/Sep/Dec"
                                else -> ""
                            },
                            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 10.sp),
                            color = if (isSelected) AppColors.onPrimary else AppColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
        
        // Show current selection
        Text(
            text = "Quarter: ${getQuarterDescription(monthOffset)}",
            style = AppTypography.Custom.statisticsLabel.copy(fontSize = 12.sp),
            color = AppColors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppSpacing.smallSpacing),
            textAlign = TextAlign.Center
        )
    }
}

private fun getQuarterDescription(monthOffset: Int): String = when (monthOffset) {
    0 -> "January, April, July, October"
    1 -> "February, May, August, November"
    2 -> "March, June, September, December"
    else -> "Unknown"
}

// Validation functions
data class ValidationResult(
    val isValid: Boolean,
    val message: String? = null
)

fun validateRecurrenceParameters(
    category: String,
    monthlySubType: String = "day",
    quarterlySubType: String = "day",
    selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    dayOfMonth: Int = 1,
    weekOfMonth: Int = 1,
    selectedDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    monthOffset: Int = 0
): ValidationResult {
    return when (category) {
        "Daily" -> ValidationResult(true)
        
        "Weekly" -> {
            if (selectedDaysOfWeek.isEmpty()) {
                ValidationResult(true, "No days selected - will default to Monday")
            } else {
                ValidationResult(true)
            }
        }
        
        "Monthly" -> {
            when (monthlySubType) {
                "day" -> validateDayOfMonth(dayOfMonth)
                "weekday" -> validateWeekdayOfMonth(weekOfMonth, selectedDayOfWeek)
                else -> ValidationResult(false, "Invalid monthly subtype")
            }
        }
        
        "Quarterly" -> {
            when (quarterlySubType) {
                "day" -> {
                    val dayValidation = validateDayOfMonth(dayOfMonth)
                    val offsetValidation = validateMonthOffset(monthOffset)
                    if (!dayValidation.isValid) dayValidation
                    else if (!offsetValidation.isValid) offsetValidation
                    else ValidationResult(true)
                }
                "weekday" -> {
                    val weekdayValidation = validateWeekdayOfMonth(weekOfMonth, selectedDayOfWeek)
                    val offsetValidation = validateMonthOffset(monthOffset)
                    if (!weekdayValidation.isValid) weekdayValidation
                    else if (!offsetValidation.isValid) offsetValidation
                    else ValidationResult(true)
                }
                else -> ValidationResult(false, "Invalid quarterly subtype")
            }
        }
        
        else -> ValidationResult(false, "Invalid recurrence category")
    }
}

private fun validateDayOfMonth(dayOfMonth: Int): ValidationResult {
    return when {
        dayOfMonth < 1 -> ValidationResult(false, "Day must be at least 1")
        dayOfMonth > 31 -> ValidationResult(false, "Day must be at most 31")
        else -> ValidationResult(true)
    }
}

private fun validateWeekdayOfMonth(weekOfMonth: Int, selectedDayOfWeek: DayOfWeek): ValidationResult {
    return when {
        weekOfMonth < 1 -> ValidationResult(false, "Week must be at least 1st")
        weekOfMonth > 4 -> ValidationResult(false, "Week must be at most 4th")
        else -> ValidationResult(true)
    }
}

private fun validateMonthOffset(monthOffset: Int): ValidationResult {
    return when {
        monthOffset < 0 -> ValidationResult(false, "Month offset must be at least 0")
        monthOffset > 2 -> ValidationResult(false, "Month offset must be at most 2")
        else -> ValidationResult(true)
    }
}

fun getValidationMessage(
    category: String,
    monthlySubType: String = "day",
    quarterlySubType: String = "day",
    selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    dayOfMonth: Int = 1,
    weekOfMonth: Int = 1,
    selectedDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    monthOffset: Int = 0
): String? {
    val validation = validateRecurrenceParameters(
        category, monthlySubType, quarterlySubType,
        selectedDaysOfWeek, dayOfMonth, weekOfMonth, selectedDayOfWeek, monthOffset
    )
    return validation.message
}

@Composable
fun AddEditHabitScreen(
    habitName: String,
    onHabitNameChange: (String) -> Unit,
    recurrenceType: RecurrenceType,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    preferredTime: LocalTime,
    onPreferredTimeChange: (LocalTime) -> Unit,
    estimatedTimeMinutes: Int,
    onEstimatedTimeMinutesChange: (Int) -> Unit,
    onSaveClicked: () -> Unit,
    onBackClicked: () -> Unit,
    onDeleteClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showTimePicker by remember { mutableStateOf(false) }
    var showRecurrencePicker by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showRecurrenceDropdown by remember { mutableStateOf(false) }
    
    // Recurrence selection state
    var selectedRecurrenceCategory by remember { mutableStateOf("Daily") }
    var monthlySubType by remember { mutableStateOf("day") } // "day" or "weekday"
    var quarterlySubType by remember { mutableStateOf("day") } // "day" or "weekday"
    
    // Weekly configuration state
    var selectedDaysOfWeek by remember { mutableStateOf(setOf<DayOfWeek>()) }
    
    // Monthly configuration state
    var dayOfMonth by remember { mutableStateOf(1) }
    var weekOfMonth by remember { mutableStateOf(1) }
    var selectedDayOfWeek by remember { mutableStateOf(DayOfWeek.MONDAY) }
    
    // Quarterly configuration state
    var monthOffset by remember { mutableStateOf(0) }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
            .padding(AppSpacing.standardSpacing)
    ) {
        // Header Row: Back button, Title, Save button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.standardRowHeight),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onBackClicked() }
            )
            
            Text(
                text = "Add/Edit Habit",
                style = AppTypography.Custom.habitNameLarge,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colors.onBackground
            )
            
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = "Save",
                modifier = Modifier
                    .size(32.dp)
                    .clickable { 
                        // Validate before saving
                        val validation = validateRecurrenceParameters(
                            category = selectedRecurrenceCategory,
                            monthlySubType = monthlySubType,
                            quarterlySubType = quarterlySubType,
                            selectedDaysOfWeek = selectedDaysOfWeek,
                            dayOfMonth = dayOfMonth,
                            weekOfMonth = weekOfMonth,
                            selectedDayOfWeek = selectedDayOfWeek,
                            monthOffset = monthOffset
                        )
                        
                        if (validation.isValid) {
                            // Create the recurrence type from current UI state
                            val newRecurrenceType = createRecurrenceType(
                                category = selectedRecurrenceCategory,
                                monthlySubType = monthlySubType,
                                quarterlySubType = quarterlySubType,
                                selectedDaysOfWeek = selectedDaysOfWeek,
                                dayOfMonth = dayOfMonth,
                                weekOfMonth = weekOfMonth,
                                selectedDayOfWeek = selectedDayOfWeek,
                                monthOffset = monthOffset
                            )
                            
                            // Update the recurrence type
                            onRecurrenceTypeChange(newRecurrenceType)
                            
                            // Call save
                            onSaveClicked()
                        }
                        // If validation fails, do nothing (validation messages are already shown in UI)
                    }
            )
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
        
        // Habit Name Input
        OutlinedTextField(
            value = habitName,
            onValueChange = onHabitNameChange,
            label = { Text("Habit Name", style = AppTypography.Custom.noteLabel) },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
        
        // Recurrence Type Section
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Top row: Recurrence label and type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left column: Recurrence label (45%)
                Box(
                    modifier = Modifier.weight(0.45f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Recurrence",
                        style = AppTypography.Custom.recurrenceText,
                        color = MaterialTheme.colors.onBackground
                    )
                }
                
                // Right column: Recurrence type dropdown (55%)
                Box(
                    modifier = Modifier.weight(0.55f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Transparent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRecurrenceDropdown = true }
                            .border(
                                width = 1.dp,
                                color = AppColors.borderMedium,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = AppSpacing.smallSpacing),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = selectedRecurrenceCategory,
                                style = AppTypography.Custom.recurrenceText,
                                color = AppColors.textAccent,
                                modifier = Modifier
                                    .padding(vertical = 4.dp)
                                    .weight(1f),
                                textAlign = TextAlign.Center
                            )
                            
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Select Recurrence",
                                modifier = Modifier.size(20.dp),
                                tint = AppColors.textAccent
                            )
                        }
                    }
                    
                    DropdownMenu(
                        expanded = showRecurrenceDropdown,
                        onDismissRequest = { showRecurrenceDropdown = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                selectedRecurrenceCategory = "Daily"
                                showRecurrenceDropdown = false
                            }
                        ) {
                            Text("Daily")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedRecurrenceCategory = "Weekly"
                                showRecurrenceDropdown = false
                            }
                        ) {
                            Text("Weekly")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedRecurrenceCategory = "Monthly"
                                showRecurrenceDropdown = false
                            }
                        ) {
                            Text("Monthly")
                        }
                        DropdownMenuItem(
                            onClick = {
                                selectedRecurrenceCategory = "Quarterly"
                                showRecurrenceDropdown = false
                            }
                        ) {
                            Text("Quarterly")
                        }
                    }
                }
            }
            
            // Bottom row: Recurrence-specific configuration
            Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
            
            when (selectedRecurrenceCategory) {
                "Daily" -> {
                    // No additional configuration needed for Daily
                }
                "Weekly" -> {
                    WeeklyConfigurationSection(
                        selectedDaysOfWeek = selectedDaysOfWeek,
                        onDaysOfWeekChange = { selectedDaysOfWeek = it }
                    )
                }
                "Monthly" -> {
                    MonthlyConfigurationSection(
                        monthlySubType = monthlySubType,
                        onMonthlySubTypeChange = { monthlySubType = it },
                        dayOfMonth = dayOfMonth,
                        onDayOfMonthChange = { dayOfMonth = it },
                        weekOfMonth = weekOfMonth,
                        onWeekOfMonthChange = { weekOfMonth = it },
                        selectedDayOfWeek = selectedDayOfWeek,
                        onSelectedDayOfWeekChange = { selectedDayOfWeek = it }
                    )
                }
                "Quarterly" -> {
                    QuarterlyConfigurationSection(
                        quarterlySubType = quarterlySubType,
                        onQuarterlySubTypeChange = { quarterlySubType = it },
                        dayOfMonth = dayOfMonth,
                        onDayOfMonthChange = { dayOfMonth = it },
                        weekOfMonth = weekOfMonth,
                        onWeekOfMonthChange = { weekOfMonth = it },
                        selectedDayOfWeek = selectedDayOfWeek,
                        onSelectedDayOfWeekChange = { selectedDayOfWeek = it },
                        monthOffset = monthOffset,
                        onMonthOffsetChange = { monthOffset = it }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
        
        // Preferred Time and Estimated Time Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left column: Preferred Time
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 80.dp), // Ensure minimum height
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp), // Match OutlinedTextField shape
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp) // Match OutlinedTextField height
                        .clickable { showTimePicker = true }
                        .border(
                            width = 1.dp,
                            color = AppColors.borderMedium, // Match other field borders
                            shape = RoundedCornerShape(4.dp)
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(AppSpacing.smallSpacing),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = preferredTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = AppTypography.Custom.timeText,
                            color = AppColors.textAccent,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        
                        Icon(
                            imageVector = Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Select Time",
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.textAccent
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                
                Text(
                    text = "Preferred Time",
                    style = AppTypography.Custom.timeLabel,
                    color = MaterialTheme.colors.onBackground
                )
            }
            
            // Right column: Estimated Time
            Column(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 80.dp), // Ensure minimum height
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    value = estimatedTimeMinutes.toString(),
                    onValueChange = { 
                        val newValue = it.toIntOrNull()
                        if (newValue != null && newValue > 0) {
                            onEstimatedTimeMinutesChange(newValue)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = AppTypography.Custom.timeText.copy(
                        textAlign = TextAlign.Center
                    ),
                    shape = RoundedCornerShape(4.dp) // Ensure consistent shape
                )
                
                Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                
                Text(
                    text = "Estimated Time (min)",
                    style = AppTypography.Custom.timeLabel,
                    color = MaterialTheme.colors.onBackground
                )
            }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
        
        // Delete Button
        DeleteButton(
            onClick = { showDeleteConfirmation = true }
        )
        
        // Time Picker Dialog
        if (showTimePicker) {
            TimePickerDialog(
                initialTime = preferredTime,
                onTimeSelected = { time ->
                    onPreferredTimeChange(time)
                    showTimePicker = false
                },
                onDismiss = { showTimePicker = false }
            )
        }
        
        // Recurrence picker is now handled by dropdown - no dialog needed
        
        // Delete Confirmation Dialog
        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Delete Habit") },
                text = { Text("Are you sure you want to delete this habit? This action cannot be undone.") },
                confirmButton = {
                    TextButton(
                        onClick = { 
                            onDeleteClicked()
                            showDeleteConfirmation = false 
                        }
                    ) {
                        Text("Delete", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: LocalTime,
    onTimeSelected: (LocalTime) -> Unit,
    onDismiss: () -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = false
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(
                        LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
                    )
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        text = {
            TimePicker(state = timePickerState)
        }
    )
}

// Recurrence picker is now handled by dropdown - no dialog needed

// Reuse the same display components from HabitDetailModal
@Composable
fun DayOfWeekDisplay(
    recurrenceType: RecurrenceType,
    modifier: Modifier = Modifier
) {
    // Hide circles for Daily recurrence type
    if (recurrenceType is RecurrenceType.Daily) {
        return
    }
    
    val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")
    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    )
    
    val selectedDays = when (recurrenceType) {
        is RecurrenceType.Weekly -> {
            // For Weekly, we need to determine which day is selected
            // This would need to be passed as a parameter, but for now we'll show Monday
            listOf(DayOfWeek.MONDAY)
        }
        is RecurrenceType.CustomWeekly -> recurrenceType.daysOfWeek
        else -> emptyList() // Hide for other types
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        dayLabels.forEachIndexed { index, label ->
            val isSelected = daysOfWeek[index] in selectedDays
            
            if (index > 0) {
                Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
            }
            
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = if (isSelected) AppColors.dayCircleSelected else AppColors.dayCircleUnselected,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = AppColors.dayCircleBorder,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = AppTypography.Custom.dayCircleText,
                    color = if (isSelected) AppColors.dayCircleTextSelected else AppColors.dayCircleTextUnselected
                )
            }
        }
    }
}

@Composable
fun MonthlyRecurrenceDisplay(
    recurrenceType: RecurrenceType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (recurrenceType) {
            is RecurrenceType.MonthlyByDate -> {
                // Simple text display for "Day X of every month"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Day ${recurrenceType.dayOfMonth}",
                        style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                        color = AppColors.textAccent
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
                    Text(
                        text = "of every month",
                        style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                        color = AppColors.textSecondary
                    )
                }
            }
            is RecurrenceType.MonthlyByWeekday -> {
                // Simple text display for "Xth DayName of every month"
                val weekLabels = listOf("1st", "2nd", "3rd", "4th", "Last")
                val dayLabels = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
                val daysOfWeek = listOf(
                    DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
                    DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
                )
                
                val weekLabel = weekLabels[recurrenceType.weekOfMonth - 1]
                val dayLabel = dayLabels[daysOfWeek.indexOf(recurrenceType.dayOfWeek)]
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "$weekLabel $dayLabel",
                        style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                        color = AppColors.textAccent
                    )
                    Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
                    Text(
                        text = "of every month",
                        style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                        color = AppColors.textSecondary
                    )
                }
            }
            else -> {
                // Fallback for other types
                Text(
                    text = "Monthly",
                    style = AppTypography.Custom.statisticsLabel,
                    color = AppColors.textAccent
                )
            }
        }
    }
}

private fun createSuperscriptText(text: String): AnnotatedString {
    return buildAnnotatedString {
        when {
            text.endsWith("st") -> {
                append(text.dropLast(2))
                withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)) {
                    append("st")
                }
            }
            text.endsWith("nd") -> {
                append(text.dropLast(2))
                withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)) {
                    append("nd")
                }
            }
            text.endsWith("rd") -> {
                append(text.dropLast(2))
                withStyle(style = SpanStyle(fontSize = 8.sp, baselineShift = BaselineShift.Superscript)) {
                    append("rd")
                }
            }
            else -> append(text)
        }
    }
}

@Composable
fun QuarterlyByDateDisplay(
    dayOfMonth: Int,
    monthOffset: Int,
    modifier: Modifier = Modifier
) {
    val monthLabels = listOf("1st", "2nd", "3rd")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Month of quarter indicator with inline label and boxes
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quarter's Months:",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textSecondary
            )
            
            Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
            
            monthLabels.forEachIndexed { index, label ->
                val isSelected = index == monthOffset
                
                if (index > 0) {
                    Spacer(modifier = Modifier.width(10.dp)) // Increased spacing
                }
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) AppColors.dayCircleSelected else AppColors.dayCircleUnselected,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = AppColors.dayCircleBorder,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = createSuperscriptText(label),
                        style = AppTypography.Custom.dayCircleText.copy(fontSize = 14.sp),
                        color = if (isSelected) AppColors.dayCircleTextSelected else AppColors.dayCircleTextUnselected
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Day display with highlighted day number
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Day $dayOfMonth",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textAccent
            )
            Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
            Text(
                text = "of the month",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textSecondary
            )
        }
    }
}

@Composable
fun QuarterlyByWeekdayDisplay(
    weekOfMonth: Int,
    dayOfWeek: DayOfWeek,
    monthOffset: Int,
    modifier: Modifier = Modifier
) {
    val weekLabels = listOf("1st", "2nd", "3rd", "4th", "Last")
    val dayLabels = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val daysOfWeek = listOf(
        DayOfWeek.SUNDAY, DayOfWeek.MONDAY, DayOfWeek.TUESDAY, 
        DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
    )
    
    val monthLabels = listOf("1st", "2nd", "3rd")
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quarter's Months with inline label and boxes (from QuarterlyByDate)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Quarter's Months:",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textSecondary
            )
            
            Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
            
            monthLabels.forEachIndexed { index, label ->
                val isSelected = index == monthOffset
                
                if (index > 0) {
                    Spacer(modifier = Modifier.width(10.dp)) // Same spacing as QuarterlyByDate
                }
                
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (isSelected) AppColors.dayCircleSelected else AppColors.dayCircleUnselected,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = AppColors.dayCircleBorder,
                            shape = RoundedCornerShape(6.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = createSuperscriptText(label),
                        style = AppTypography.Custom.dayCircleText.copy(fontSize = 14.sp),
                        color = if (isSelected) AppColors.dayCircleTextSelected else AppColors.dayCircleTextUnselected
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
        
        // Weekday text display (from MonthlyByWeekday style)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            val weekLabel = weekLabels[weekOfMonth - 1]
            val dayLabel = dayLabels[daysOfWeek.indexOf(dayOfWeek)]
            
            Text(
                text = "$weekLabel $dayLabel",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textAccent
            )
            Spacer(modifier = Modifier.width(AppSpacing.smallSpacing))
            Text(
                text = "of every month",
                style = AppTypography.Custom.statisticsLabel.copy(fontSize = AppTypography.Custom.statisticsLabel.fontSize * 1.6),
                color = AppColors.textSecondary
            )
        }
    }
}

/**
 * Converts UI state selections to a RecurrenceType object
 */
fun createRecurrenceType(
    category: String,
    monthlySubType: String = "day",
    quarterlySubType: String = "day",
    selectedDaysOfWeek: Set<DayOfWeek> = emptySet(),
    dayOfMonth: Int = 1,
    weekOfMonth: Int = 1,
    selectedDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    monthOffset: Int = 0
): RecurrenceType {
    return when (category) {
        "Daily" -> RecurrenceType.Daily
        
        "Weekly" -> {
            if (selectedDaysOfWeek.isEmpty()) {
                // Default to Monday if no days selected
                RecurrenceType.Weekly
            } else {
                // Custom weekly with specific days
                RecurrenceType.CustomWeekly(selectedDaysOfWeek.toList())
            }
        }
        
        "Monthly" -> {
            when (monthlySubType) {
                "day" -> RecurrenceType.MonthlyByDate(dayOfMonth)
                "weekday" -> RecurrenceType.MonthlyByWeekday(weekOfMonth, selectedDayOfWeek)
                else -> RecurrenceType.MonthlyByDate(dayOfMonth)
            }
        }
        
        "Quarterly" -> {
            when (quarterlySubType) {
                "day" -> RecurrenceType.QuarterlyByDate(dayOfMonth, monthOffset)
                "weekday" -> RecurrenceType.QuarterlyByWeekday(weekOfMonth, selectedDayOfWeek, monthOffset)
                else -> RecurrenceType.QuarterlyByDate(dayOfMonth, monthOffset)
            }
        }
        
        else -> RecurrenceType.Daily // Default fallback
    }
}

@Preview(showBackground = true)
@Composable
fun AddEditHabitScreenPreview() {
    MaterialTheme {
        var habitName by remember { mutableStateOf("Read a Book") }
        var recurrenceType by remember { mutableStateOf<RecurrenceType>(RecurrenceType.Weekly) }
        var preferredTime by remember { mutableStateOf(LocalTime.of(10, 0)) }
        var estimatedTime by remember { mutableStateOf(30) }
        
        AddEditHabitScreen(
            habitName = habitName,
            onHabitNameChange = { habitName = it },
            recurrenceType = recurrenceType,
            onRecurrenceTypeChange = { recurrenceType = it },
            preferredTime = preferredTime,
            onPreferredTimeChange = { preferredTime = it },
            estimatedTimeMinutes = estimatedTime,
            onEstimatedTimeMinutesChange = { estimatedTime = it },
            onSaveClicked = { },
            onBackClicked = { },
            onDeleteClicked = { }
        )
    }
}
