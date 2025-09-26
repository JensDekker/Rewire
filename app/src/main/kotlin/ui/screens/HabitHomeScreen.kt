package com.example.rewire.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.manager.HabitManager
import com.example.rewire.ui.components.AddButton
import com.example.rewire.ui.components.HabitCard
import com.example.rewire.ui.theme.AppSpacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HabitHomeScreen(
    habitManager: HabitManager,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now().toString()
    val coroutineScope = rememberCoroutineScope()
    
    // State for habits due today
    var habitsDueToday by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }
    var completedHabitIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var habitNotes by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var expandedNoteHabits by remember { mutableStateOf<Set<Long>>(emptySet()) }
    
    // Load habits due today
    LaunchedEffect(today) {
        habitsDueToday = habitManager.getHabitsDueOn(today)
        
        // Load completion status for each habit
        val completedIds = mutableSetOf<Long>()
        for (habit in habitsDueToday) {
            val completions = habitManager.getCompletionsForHabit(habit.id)
            if (completions.any { it.date == today }) {
                completedIds.add(habit.id)
            }
        }
        completedHabitIds = completedIds
        
        // Load notes for each habit
        val notes = mutableMapOf<Long, String>()
        for (habit in habitsDueToday) {
            val note = habitManager.getNoteForHabitOnDate(habit.id, today)
            notes[habit.id] = note
        }
        habitNotes = notes
    }
    
    // Sort habits by preferred time (chronological order)
    val sortedHabits = habitsDueToday.sortedBy { habit ->
        try {
            LocalTime.parse(habit.preferredTime)
        } catch (e: Exception) {
            LocalTime.of(9, 0) // Default to 9:00 AM if parsing fails
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(AppSpacing.standardSpacing)
    ) {
        // Header
        Text(
            text = "Today's Habits",
            style = MaterialTheme.typography.h4,
            fontSize = 28.sp,
            modifier = Modifier.padding(bottom = AppSpacing.standardSpacing)
        )
        
        if (sortedHabits.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.standardSpacing)
                ) {
                    Text(
                        text = "No habits scheduled for today",
                        style = MaterialTheme.typography.body1,
                        fontSize = 16.sp
                    )
                    AddButton(
                        onClick = {
                            // TODO: Navigate to add habit screen
                        }
                    )
                }
            }
        } else {
            // List of habits
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
            ) {
                items(sortedHabits) { habit ->
                    val isComplete = completedHabitIds.contains(habit.id)
                    val noteText = habitNotes[habit.id] ?: ""
                    val isNoteFieldVisible = expandedNoteHabits.contains(habit.id)
                    
                    HabitCard(
                        habitName = habit.name,
                        isComplete = isComplete,
                        noteText = noteText,
                        onNoteTextChange = { newNote ->
                            habitNotes = habitNotes + (habit.id to newNote)
                            // Save note to database
                            if (newNote.isNotBlank()) {
                                coroutineScope.launch {
                                    val noteEntity = com.example.rewire.db.entity.HabitNoteEntity(
                                        habitId = habit.id,
                                        content = newNote,
                                        timestamp = today
                                    )
                                    habitManager.insertNote(noteEntity)
                                }
                            }
                        },
                        isNoteFieldVisible = isNoteFieldVisible,
                        onCardClicked = {
                            // TODO: Navigate to habit detail screen
                        },
                        onCheckClicked = {
                            coroutineScope.launch {
                                if (isComplete) {
                                    // Remove completion
                                    habitManager.deleteCompletion(habit.id, today)
                                    completedHabitIds = completedHabitIds - habit.id
                                } else {
                                    // Add completion
                                    habitManager.completeHabit(habit.id, today)
                                    completedHabitIds = completedHabitIds + habit.id
                                }
                            }
                        },
                        onAddNoteClicked = {
                            expandedNoteHabits = if (isNoteFieldVisible) {
                                expandedNoteHabits - habit.id
                            } else {
                                expandedNoteHabits + habit.id
                            }
                        }
                    )
                }
                
                // Add button at the end
                item {
                    AddButton(
                        onClick = {
                            // TODO: Navigate to add habit screen
                        }
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitHomeScreenPreview() {
    MaterialTheme {
        // Create mock data for preview
        val mockHabits = listOf(
            HabitEntity(
                id = 1,
                name = "Morning Meditation",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "07:00",
                estimatedMinutes = 15,
                startDate = LocalDate.now().toString()
            ),
            HabitEntity(
                id = 2,
                name = "Read a Book",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "20:00",
                estimatedMinutes = 30,
                startDate = LocalDate.now().toString()
            ),
            HabitEntity(
                id = 3,
                name = "Exercise",
                recurrence = com.example.rewire.core.RecurrenceType.Daily,
                preferredTime = "18:00",
                estimatedMinutes = 45,
                startDate = LocalDate.now().toString()
            )
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(AppSpacing.standardSpacing)
        ) {
            Text(
                text = "Today's Habits",
                style = MaterialTheme.typography.h4,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = AppSpacing.standardSpacing)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
            ) {
                items(mockHabits) { habit ->
                    HabitCard(
                        habitName = habit.name,
                        isComplete = habit.id == 1L, // First habit is completed
                        noteText = if (habit.id == 1L) "Great meditation session!" else "",
                        onNoteTextChange = { },
                        isNoteFieldVisible = habit.id == 1L,
                        onCardClicked = { },
                        onCheckClicked = { },
                        onAddNoteClicked = { }
                    )
                }
                
                item {
                    AddButton(onClick = { })
                }
            }
        }
    }
}
