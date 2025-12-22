package com.example.rewire.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.example.rewire.db.entity.HabitEntity
import com.example.rewire.manager.HabitManager
import com.example.rewire.ui.components.AddButton
import com.example.rewire.ui.components.HabitCard
import com.example.rewire.ui.components.HabitDetailModal
import com.example.rewire.ui.screens.AddEditHabitScreen
import com.example.rewire.db.entity.toCore
import com.example.rewire.db.entity.toEntity
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.core.Habit
import com.example.rewire.ui.theme.AppSpacing
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HabitHomeScreen(
    habitManager: HabitManager,
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now().toString()
    val coroutineScope = rememberCoroutineScope()
    
    // State for habits due today
    var habitsDueToday by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }
    var allHabits by remember { mutableStateOf<List<HabitEntity>>(emptyList()) }
    var completedHabitIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    var habitNotes by remember { mutableStateOf<Map<Long, String>>(emptyMap()) }
    var expandedNoteHabits by remember { mutableStateOf<Set<Long>>(emptySet()) }
    
    // Label state - map of habitId to list of labels
    var habitLabels by remember { mutableStateOf<Map<Long, List<LabelEntity>>>(emptyMap()) }
    
    // Navigation state
    var showAddEditScreen by remember { mutableStateOf(false) }
    var editingHabit by remember { mutableStateOf<HabitEntity?>(null) }
    var showHabitDetailModal by remember { mutableStateOf(false) }
    var selectedHabit by remember { mutableStateOf<HabitEntity?>(null) }
    
    // Load habits due today and all habits
    LaunchedEffect(today) {
        // Load all habits first
        allHabits = habitManager.getHabits()
        habitsDueToday = habitManager.getHabitsDueOn(today)
        
        // Load completion status for each habit due today
        val completedIds = mutableSetOf<Long>()
        for (habit in habitsDueToday) {
            val completions = habitManager.getCompletionsForHabit(habit.id)
            if (completions.any { it.date == today }) {
                completedIds.add(habit.id)
            }
        }
        completedHabitIds = completedIds
        
        // Load notes for each habit due today
        val notes = mutableMapOf<Long, String>()
        for (habit in habitsDueToday) {
            val note = habitManager.getNoteForHabitOnDate(habit.id, today)
            notes[habit.id] = note
        }
        habitNotes = notes
    }
    
    // Load labels for habits (optimized batch loading)
    LaunchedEffect(allHabits) {
        if (allHabits.isEmpty()) {
            habitLabels = emptyMap()
            return@LaunchedEffect
        }
        // Use batch loading to avoid N+1 queries - load labels for all habits
        val habitIds = allHabits.map { it.id }
        habitLabels = habitManager.getLabelsForHabits(habitIds)
    }
    
    // Sort habits by preferred time (chronological order)
    val sortedHabits = habitsDueToday.sortedBy { habit ->
        try {
            LocalTime.parse(habit.preferredTime)
        } catch (e: Exception) {
            LocalTime.of(9, 0) // Default to 9:00 AM if parsing fails
        }
    }
    
    // Get habits that aren't due today and sort them alphabetically
    val habitsDueTodayIds = habitsDueToday.map { it.id }.toSet()
    val otherHabits = allHabits.filter { it.id !in habitsDueTodayIds }.sortedBy { it.name }
    
    // State for menu
    var showMenu by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Today's Habits") },
                actions = {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                navController?.navigate("label_management")
                            }
                        ) {
                            Text("Manage Labels")
                        }
                        // Future menu items can be added here:
                        // DropdownMenuItem(onClick = { ... }) { Text("Settings") }
                        // DropdownMenuItem(onClick = { ... }) { Text("Statistics") }
                    }
                }
            )
        }
    ) { padding ->
        // Use LazyColumn for scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppSpacing.standardSpacing),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
        ) {
            // Today's Habits Section
            if (sortedHabits.isNotEmpty()) {
                items(sortedHabits) { habit ->
                    val isComplete = completedHabitIds.contains(habit.id)
                    val noteText = habitNotes[habit.id] ?: ""
                    val isNoteFieldVisible = expandedNoteHabits.contains(habit.id)
                    val labels = habitLabels[habit.id]?.map { it.toCore() } ?: emptyList()
                    
                    HabitCard(
                        habitName = habit.name,
                        isComplete = isComplete,
                        noteText = noteText,
                        labels = labels,
                        navController = navController,
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
                            selectedHabit = habit
                            showHabitDetailModal = true
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
                        },
                        onEditClicked = {
                            editingHabit = habit
                            showAddEditScreen = true
                        }
                    )
                }
            }
            
            // All Other Habits Section
            if (otherHabits.isNotEmpty()) {
                // Section header
                item {
                    Text(
                        text = "All Other Habits",
                        style = MaterialTheme.typography.h5,
                        fontSize = 20.sp,
                        modifier = Modifier.padding(
                            top = if (sortedHabits.isNotEmpty()) AppSpacing.standardSpacing else 0.dp,
                            bottom = AppSpacing.smallSpacing
                        )
                    )
                }
                
                items(otherHabits) { habit ->
                    val labels = habitLabels[habit.id]?.map { it.toCore() } ?: emptyList()
                    
                    HabitCard(
                        habitName = habit.name,
                        isComplete = false, // Other habits are never marked complete
                        noteText = "",
                        onNoteTextChange = { },
                        isNoteFieldVisible = false,
                        labels = labels,
                        navController = navController,
                        onCardClicked = {
                            selectedHabit = habit
                            showHabitDetailModal = true
                        },
                        onCheckClicked = {
                            // No completion action for other habits
                        },
                        onAddNoteClicked = {
                            // No note action for other habits
                        },
                        onEditClicked = {
                            editingHabit = habit
                            showAddEditScreen = true
                        }
                    )
                }
            }
            
            // Show empty state if no habits at all
            if (sortedHabits.isEmpty() && otherHabits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.largeSpacing),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No habits yet. Click the '+' button to add your first habit!",
                            style = MaterialTheme.typography.body1,
                            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            
            // Add button at the end
            item {
                Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
                AddButton(
                    onClick = {
                        editingHabit = null // New habit
                        showAddEditScreen = true
                    }
                )
            }
        }
    }
    
    // Show AddEditHabitScreen when navigation state is active
    if (showAddEditScreen) {
        // Local state for the form
        var habitName by remember(editingHabit) { mutableStateOf(editingHabit?.name ?: "") }
        var recurrenceType by remember(editingHabit) { mutableStateOf(editingHabit?.recurrence ?: com.example.rewire.core.RecurrenceType.Daily) }
        var preferredTime by remember(editingHabit) { 
            mutableStateOf(
                try { 
                    LocalTime.parse(editingHabit?.preferredTime ?: "09:00") 
                } catch (e: Exception) { 
                    LocalTime.of(9, 0) 
                }
            )
        }
        var estimatedTimeMinutes by remember(editingHabit) { mutableStateOf(editingHabit?.estimatedMinutes ?: 10) }
        
                AddEditHabitScreen(
                    habitName = habitName,
                    onHabitNameChange = { habitName = it },
                    recurrenceType = recurrenceType,
                    onRecurrenceTypeChange = { recurrenceType = it },
                    preferredTime = preferredTime,
                    onPreferredTimeChange = { preferredTime = it },
                    estimatedTimeMinutes = estimatedTimeMinutes,
                    onEstimatedTimeMinutesChange = { estimatedTimeMinutes = it },
                    habitManager = habitManager,
                    editingHabit = editingHabit,
                    navController = navController,
            onSaveClicked = {
                // Save logic is now handled inside AddEditHabitScreen with atomic methods
                // This callback is called after successful save to refresh and close
                coroutineScope.launch {
                    // Refresh the habits list
                    allHabits = habitManager.getHabits()
                    habitsDueToday = habitManager.getHabitsDueOn(today)
                    
                    showAddEditScreen = false
                    editingHabit = null
                }
            },
            onBackClicked = {
                showAddEditScreen = false
                editingHabit = null
            },
            onDeleteClicked = {
                editingHabit?.let { habit ->
                    coroutineScope.launch {
                        habitManager.deleteHabit(habit)
                        
                        // Refresh the habits list
                        allHabits = habitManager.getHabits()
                        habitsDueToday = habitManager.getHabitsDueOn(today)
                        
                        showAddEditScreen = false
                        editingHabit = null
                    }
                }
            }
        )
    }
    
    // Show HabitDetailModal when navigation state is active
    if (showHabitDetailModal && selectedHabit != null) {
        val habit = selectedHabit!!
        val isComplete = completedHabitIds.contains(habit.id)
        val noteText = habitNotes[habit.id] ?: ""
        
        // Dark background overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { 
                    showHabitDetailModal = false
                    selectedHabit = null
                },
            contentAlignment = Alignment.Center
        ) {
            HabitDetailModal(
            habitName = habit.name,
            isComplete = isComplete,
            completionStreak = 0, // TODO: Calculate actual streak
            percentageComplete = 0.0, // TODO: Calculate actual percentage
            recurrenceType = habit.recurrence,
            preferredTime = habit.preferredTime,
            estimatedTimeMinutes = habit.estimatedMinutes,
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
            onEditClicked = {
                showHabitDetailModal = false
                editingHabit = selectedHabit
                showAddEditScreen = true
            }
        )
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
