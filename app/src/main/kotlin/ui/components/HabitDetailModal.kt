package com.example.rewire.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import com.example.rewire.core.RecurrenceType
import com.example.rewire.core.DayOfWeek
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppColors

@Composable
fun DayOfWeekDisplay(
    recurrenceType: RecurrenceType,
    modifier: Modifier = Modifier
) {
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
        is RecurrenceType.Daily -> daysOfWeek // All days for daily
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
                    style = MaterialTheme.typography.body2,
                    color = if (isSelected) AppColors.dayCircleTextSelected else AppColors.dayCircleTextUnselected,
                    fontSize = 14.sp
                )
            }
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
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.weight(1f),
                    fontSize = 28.sp
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
                        style = MaterialTheme.typography.h4,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Completion Streak",
                        style = MaterialTheme.typography.body2,
                        fontSize = 14.sp
                    )
                }
                
                // Right column: Percentage Complete
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${String.format("%.1f", percentageComplete)}%",
                        style = MaterialTheme.typography.h4,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Percentage Complete",
                        style = MaterialTheme.typography.body2,
                        fontSize = 14.sp
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
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing)
                ) {
                    // Left column: Recurrence label (45%)
                    Column(
                        modifier = Modifier.weight(0.45f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Recurrence",
                            style = MaterialTheme.typography.body1,
                            fontSize = 24.sp
                        )
                    }
                    
                    // Right column: Recurrence type (55%)
                    Column(
                        modifier = Modifier.weight(0.55f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = when (recurrenceType) {
                                is RecurrenceType.Daily -> "Daily"
                                is RecurrenceType.Weekly -> "Weekly"
                                is RecurrenceType.CustomWeekly -> "Custom Weekly"
                                is RecurrenceType.MonthlyByDate -> "Monthly"
                                is RecurrenceType.MonthlyByWeekday -> "Monthly"
                                is RecurrenceType.QuarterlyByDate -> "Quarterly"
                                is RecurrenceType.QuarterlyByWeekday -> "Quarterly"
                            },
                            style = MaterialTheme.typography.body1,
                            fontSize = 24.sp,
                            color = AppColors.textAccent
                        )
                    }
                }
                
                // Bottom row: Day of week display (only for Weekly types)
                if (recurrenceType is RecurrenceType.Weekly || 
                    recurrenceType is RecurrenceType.CustomWeekly ||
                    recurrenceType is RecurrenceType.Daily) {
                    Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                    DayOfWeekDisplay(recurrenceType = recurrenceType)
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
                        style = MaterialTheme.typography.h4,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Preferred Time",
                        style = MaterialTheme.typography.body2,
                        fontSize = 14.sp
                    )
                }
                
                // Right column: Estimated Time
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${estimatedTimeMinutes}m",
                        style = MaterialTheme.typography.h4,
                        fontSize = 32.sp
                    )
                    Text(
                        text = "Estimated Time",
                        style = MaterialTheme.typography.body2,
                        fontSize = 14.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
            
            // Row 5: Today's Notes
            OutlinedTextField(
                value = noteText,
                onValueChange = onNoteTextChange,
                label = { Text("Today's Note") },
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
            recurrenceType = RecurrenceType.CustomWeekly(listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY)),
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
