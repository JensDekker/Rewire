package com.example.rewire.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
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
import com.example.rewire.ui.components.LabelChip
import com.example.rewire.ui.screens.AddEditHabitScreen
import com.example.rewire.db.entity.toCore
import com.example.rewire.db.entity.toEntity
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.core.Habit
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun HabitHomeScreen(
    habitManager: HabitManager,
    navController: NavController? = null,
    modifier: Modifier = Modifier,
    topSpacing: androidx.compose.ui.unit.Dp = 20.dp // Vertical spacing above title header
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
    
    // Filter state - selected label IDs for filtering
    var selectedFilterLabelIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
    
    // All available labels for filter UI
    var allAvailableLabels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }
    
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
    
    // Load all available labels for filter UI
    // Reload when habits change (in case labels were added/removed)
    LaunchedEffect(allHabits) {
        try {
            allAvailableLabels = habitManager.getAllLabels()
        } catch (e: Exception) {
            // Handle error silently - filter UI will just be empty
            allAvailableLabels = emptyList()
        }
    }
    
    // Sort habits by preferred time (chronological order)
    val sortedHabitsBase = habitsDueToday.sortedBy { habit ->
        try {
            LocalTime.parse(habit.preferredTime)
        } catch (e: Exception) {
            LocalTime.of(9, 0) // Default to 9:00 AM if parsing fails
        }
    }
    
    // Get habits that aren't due today and sort them alphabetically
    val habitsDueTodayIds = habitsDueToday.map { it.id }.toSet()
    val otherHabitsBase = allHabits.filter { it.id !in habitsDueTodayIds }.sortedBy { it.name }
    
    // Filter logic: filter habits by selected labels (OR logic - habit matches if it has ANY selected label)
    val filterHabits: (List<HabitEntity>) -> List<HabitEntity> = { habits ->
        if (selectedFilterLabelIds.isEmpty()) {
            habits
        } else {
            habits.filter { habit ->
                val habitLabelIds = habitLabels[habit.id]?.map { it.id }?.toSet() ?: emptySet()
                habitLabelIds.intersect(selectedFilterLabelIds).isNotEmpty()
            }
        }
    }
    
    // Apply filter to both habit lists
    val sortedHabits = filterHabits(sortedHabitsBase)
    val otherHabits = filterHabits(otherHabitsBase)
    
    // State for menu
    var showMenu by remember { mutableStateOf(false) }
    
    // Custom header with unified background
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top spacer for title header
        Spacer(modifier = Modifier.height(topSpacing))
        
        // Custom header row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.standardSpacing, vertical = AppSpacing.standardSpacing),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Spacer to balance the settings icon on the right
            Spacer(modifier = Modifier.width(48.dp)) // Width of IconButton for centering
            
            // Centered title
            Text(
                text = "Today's Habits",
                style = AppTypography.materialTypography.h4.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 28.sp, // Double the typical TopAppBar title size (20sp)
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Settings icon button
            IconButton(onClick = { showMenu = true }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
            
            // Dropdown menu (positioned relative to settings icon)
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
        
        // Main content - Use LazyColumn for scrollable content
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppSpacing.standardSpacing),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
        ) {
            // Filter UI Section
            if (allAvailableLabels.isNotEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.smallSpacing)
                    ) {
                        // Filter header with clear button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (selectedFilterLabelIds.isEmpty()) {
                                    "Filter by Label"
                                } else {
                                    "Filtered by ${selectedFilterLabelIds.size} ${if (selectedFilterLabelIds.size == 1) "label" else "labels"}"
                                },
                                style = AppTypography.materialTypography.subtitle2,
                                modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
                            )
                            
                            if (selectedFilterLabelIds.isNotEmpty()) {
                                TextButton(
                                    onClick = { selectedFilterLabelIds = emptySet() },
                                    modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Clear filters",
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Clear")
                                }
                            }
                        }
                        
                        // Filter chips - selectable labels
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = AppSpacing.smallSpacing),
                            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                        ) {
                            allAvailableLabels.forEach { labelEntity ->
                                val label = labelEntity.toCore()
                                val isSelected = selectedFilterLabelIds.contains(label.id)
                                
                                LabelChip(
                                    label = label,
                                    onClick = {
                                        selectedFilterLabelIds = if (isSelected) {
                                            selectedFilterLabelIds - label.id
                                        } else {
                                            selectedFilterLabelIds + label.id
                                        }
                                    },
                                    modifier = Modifier
                                        .then(
                                            if (isSelected) {
                                                Modifier.border(
                                                    2.dp,
                                                    AppColors.primary,
                                                    AppShapes.cardShape
                                                )
                                            } else {
                                                Modifier
                                            }
                                        )
                                )
                            }
                        }
                    }
                }
            }
            
            // Today's Habits Section
            if (sortedHabits.isNotEmpty()) {
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
                        labels = habitLabels[habit.id]?.map { it.toCore() } ?: emptyList(),
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
                    HabitCard(
                        habitName = habit.name,
                        isComplete = false, // Other habits are never marked complete
                        noteText = "",
                        onNoteTextChange = { },
                        isNoteFieldVisible = false,
                        labels = habitLabels[habit.id]?.map { it.toCore() } ?: emptyList(),
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
            
            // Show empty state
            if (sortedHabits.isEmpty() && otherHabits.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppSpacing.largeSpacing),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = AppSpacing.standardSpacing)
                        ) {
                                Text(
                                    text = if (selectedFilterLabelIds.isEmpty()) {
                                        "No habits yet. Click the '+' button to add your first habit!"
                                    } else {
                                        "No habits match the selected labels. Try selecting different labels or clear the filter."
                                    },
                                    style = AppTypography.materialTypography.body1,
                                    color = AppColors.textSecondary,
                                    textAlign = TextAlign.Center
                                )
                            if (selectedFilterLabelIds.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                                TextButton(onClick = { selectedFilterLabelIds = emptySet() }) {
                                    Text("Clear Filter")
                                }
                            }
                        }
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
                    
                    // Explicitly reload labels after updating habits to ensure card colors update immediately
                    if (allHabits.isNotEmpty()) {
                        val habitIds = allHabits.map { it.id }
                        habitLabels = habitManager.getLabelsForHabits(habitIds)
                    } else {
                        habitLabels = emptyMap()
                    }
                    
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
                        
                        // Explicitly reload labels after deleting habit
                        if (allHabits.isNotEmpty()) {
                            val habitIds = allHabits.map { it.id }
                            habitLabels = habitManager.getLabelsForHabits(habitIds)
                        } else {
                            habitLabels = emptyMap()
                        }
                        
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

@Preview(showBackground = true, name = "Habit Home Screen Preview")
@Composable
fun HabitHomeScreenPreview() {
    MaterialTheme {
        // Note: This preview shows a simplified version since HabitHomeScreen requires HabitManager
        // For full preview, you would need to create a mock HabitManager or use dependency injection
        
        // Preview the header structure
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background)
        ) {
            // Custom header row (matches actual implementation)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppSpacing.standardSpacing, vertical = AppSpacing.standardSpacing),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Spacer to balance the settings icon on the right
                Spacer(modifier = Modifier.width(48.dp))
                
                // Centered title
                Text(
                    text = "Today's Habits",
                    style = AppTypography.materialTypography.h4.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = 28.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                
                // Settings icon button
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
            
            // Preview content area
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppSpacing.standardSpacing),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
            ) {
                item {
                    Text(
                        text = "Preview: Full screen requires HabitManager",
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(AppSpacing.standardSpacing),
                        color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
