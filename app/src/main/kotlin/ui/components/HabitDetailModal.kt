package com.example.rewire.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.RadioButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.BaselineShift
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography

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
                    .size(37.dp)
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

@Composable
fun HabitDetailModal(
    habitName: String,
    isComplete: Boolean,
    completionStreak: Int,
    percentageComplete: Double,
    recurrenceType: RecurrenceType,
    preferredTime: String,
    estimatedTimeMinutes: Int,
    noteText: String,
    onNoteTextChange: (String) -> Unit,
    onCheckClicked: () -> Unit = {},
    onEditClicked: () -> Unit = {},
    onKeyboardUpClicked: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AppSpacing.standardSpacing),
        shape = AppShapes.largeCardShape,
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.modalPadding)
        ) {
            // Row 1: Habit name, checkbox, edit icon, keyboard up arrow
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AppSpacing.standardRowHeight),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = habitName,
                    style = AppTypography.Custom.habitNameLarge,
                    modifier = Modifier.weight(1f)
                )
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing)
                ) {
                    Icon(
                        imageVector = if (isComplete) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = if (isComplete) "Completed" else "Incomplete",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onCheckClicked() }
                    )
                    
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onEditClicked() }
                    )
                    
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Keyboard Up",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { onKeyboardUpClicked() }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
            
            // Row 2: Completion Streak and Percentage Complete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing)
            ) {
                // Left column: Completion Streak
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = completionStreak.toString(),
                        style = AppTypography.Custom.statisticsNumber
                    )
                    Text(
                        text = "Completion Streak",
                        style = AppTypography.Custom.statisticsLabel
                    )
                }
                
                // Right column: Percentage Complete
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${String.format("%.1f", percentageComplete)}%",
                        style = AppTypography.Custom.statisticsNumber
                    )
                    Text(
                        text = "Percentage Complete",
                        style = AppTypography.Custom.statisticsLabel
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Row 3: Recurrence Type
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Top row: Recurrence label and type
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left column: Recurrence label (40%)
                    Box(
                        modifier = Modifier.weight(0.45f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Recurrence",
                            style = AppTypography.Custom.recurrenceText
                        )
                    }
                    
                    // Right column: Recurrence type (60%)
                    val recurrenceText = when (recurrenceType) {
                        is RecurrenceType.Daily -> "Daily"
                        is RecurrenceType.Weekly -> "Weekly"
                        is RecurrenceType.CustomWeekly -> "Custom Weekly"
                        is RecurrenceType.MonthlyByDate -> "Monthly"
                        is RecurrenceType.MonthlyByWeekday -> "Monthly"
                        is RecurrenceType.QuarterlyByDate -> "Quarterly"
                        is RecurrenceType.QuarterlyByWeekday -> "Quarterly"
                    }
                    
                    // Add outline for all recurrence types - tied to the 60% column
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Transparent,
                            modifier = Modifier
                                .weight(0.60f)
                                .border(
                                    width = 1.dp,
                                    color = AppColors.borderMedium,
                                    shape = RoundedCornerShape(8.dp)
                                )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = recurrenceText,
                                    style = AppTypography.Custom.recurrenceText,
                                    color = AppColors.textAccent,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                }
                
                // Bottom row: Recurrence-specific display
                when (recurrenceType) {
                    is RecurrenceType.Weekly, 
                    is RecurrenceType.CustomWeekly,
                    is RecurrenceType.Daily -> {
                        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing + 2.dp))
                        DayOfWeekDisplay(recurrenceType = recurrenceType)
                    }
                    is RecurrenceType.MonthlyByDate,
                    is RecurrenceType.MonthlyByWeekday -> {
                        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                        MonthlyRecurrenceDisplay(recurrenceType = recurrenceType)
                    }
                    is RecurrenceType.QuarterlyByDate -> {
                        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                        QuarterlyByDateDisplay(
                            dayOfMonth = recurrenceType.dayOfMonth,
                            monthOffset = recurrenceType.monthOffset
                        )
                    }
                    is RecurrenceType.QuarterlyByWeekday -> {
                        Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                        QuarterlyByWeekdayDisplay(
                            weekOfMonth = recurrenceType.weekOfMonth,
                            dayOfWeek = recurrenceType.dayOfWeek,
                            monthOffset = recurrenceType.monthOffset
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Row 4: Preferred Time and Estimated Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing)
            ) {
                // Left column: Preferred Time
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = preferredTime,
                        style = AppTypography.Custom.timeText
                    )
                    Text(
                        text = "Preferred Time",
                        style = AppTypography.Custom.timeLabel
                    )
                }
                
                // Right column: Estimated Time
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${estimatedTimeMinutes}m",
                        style = AppTypography.Custom.timeText
                    )
                    Text(
                        text = "Estimated Time",
                        style = AppTypography.Custom.timeLabel
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
            
            // Row 5: Today's Notes
                OutlinedTextField(
                    value = noteText,
                    onValueChange = onNoteTextChange,
                    label = { Text("Today's Note", style = AppTypography.Custom.noteLabel) },
                    modifier = Modifier.fillMaxWidth()
                )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitDetailModalPreview() {
    MaterialTheme {
        var note by remember { mutableStateOf("This is today's note for the habit.") }
        
        HabitDetailModal(
            habitName = "Read a Book",
            isComplete = false,
            completionStreak = 7,
            percentageComplete = 85.3,
            recurrenceType = RecurrenceType.QuarterlyByWeekday(2, DayOfWeek.MONDAY, 1), // 2nd Monday of 2nd month of every quarter
            preferredTime = "10:00 AM",
            estimatedTimeMinutes = 30,
            noteText = note,
            onNoteTextChange = { note = it },
            onCheckClicked = { },
            onEditClicked = { },
            onKeyboardUpClicked = { }
        )
    }
}
