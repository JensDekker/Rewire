package com.example.rewire.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun HabitDetailModal(
    habitName: String,
    isComplete: Boolean,
    completionStreak: Int,
    percentageComplete: Double,
    recurrenceType: String,
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
                        imageVector = Icons.Filled.Edit,
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
            
            Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
            
            // Row 3: Recurrence Type
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = recurrenceType,
                    style = MaterialTheme.typography.body1,
                    fontSize = 16.sp
                )
            }
            
            Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
            
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
            recurrenceType = "Daily",
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
