package com.example.rewire.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.toCore
import com.example.rewire.manager.HabitManager
import com.example.rewire.repository.LabelResult
import com.example.rewire.ui.components.LabelChip
import com.example.rewire.ui.components.CreateEditLabelDialog
import com.example.rewire.ui.components.DeleteLabelDialog
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppTypography
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.util.Log

@Composable
fun LabelManagementScreen(
    navController: NavController,
    habitManager: HabitManager,
    selectedLabelId: Long? = null,  // Optional: pre-select a label
    modifier: Modifier = Modifier,
    topSpacing: androidx.compose.ui.unit.Dp = 20.dp // Vertical spacing above title header
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // State
    var labels by remember { mutableStateOf<List<LabelEntity>>(emptyList()) }
    var labelUsageCounts by remember { mutableStateOf<Map<Long, Int>>(emptyMap()) }
    var isLoading by remember { mutableStateOf(true) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var editingLabel by remember { mutableStateOf<LabelEntity?>(null) }
    var labelToDelete by remember { mutableStateOf<LabelEntity?>(null) }
    
    // Load labels
    LaunchedEffect(Unit) {
        try {
            labels = habitManager.getAllLabels()
            
            // Load usage counts for each label
            val usageMap = mutableMapOf<Long, Int>()
            labels.forEach { label ->
                // Get habit IDs that use this label
                val habitIds = habitManager.getHabitIdsWithLabel(label.id)
                usageMap[label.id] = habitIds.size
            }
            labelUsageCounts = usageMap
        } catch (e: Exception) {
            Log.e("LabelManagementScreen", "Failed to load labels: ${e.message}", e)
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Failed to load labels: ${e.message}",
                    duration = SnackbarDuration.Long
                )
            }
        } finally {
            isLoading = false
        }
    }
    
    // Reload labels when returning from create/edit dialogs
    val refreshLabels: () -> Unit = {
        coroutineScope.launch {
            try {
                labels = habitManager.getAllLabels()
                // Reload usage counts
                val usageMap = mutableMapOf<Long, Int>()
                labels.forEach { label ->
                    val habitIds = habitManager.getHabitIdsWithLabel(label.id)
                    usageMap[label.id] = habitIds.size
                }
                labelUsageCounts = usageMap
            } catch (e: Exception) {
                Log.e("LabelManagementScreen", "Failed to refresh labels: ${e.message}", e)
                snackbarHostState.showSnackbar(
                    message = "Failed to refresh labels: ${e.message}",
                    duration = SnackbarDuration.Long
                )
            }
        }
    }
    
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
            // Back button
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
            
            // Centered title
            Text(
                text = "Manage Labels",
                style = AppTypography.materialTypography.h4.copy(
                    fontWeight = FontWeight.Bold
                ),
                fontSize = 28.sp,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            
            // Add button
            IconButton(onClick = { showCreateDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Create Label"
                )
            }
        }
        
        // Content area
        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppSpacing.standardSpacing)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(AppSpacing.standardSpacing)
                        )
                    }
                } else if (labels.isEmpty()) {
                    // Empty state
                    EmptyLabelsState(
                        onCreateClick = { showCreateDialog = true },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Labels list
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                    ) {
                        items(labels) { label ->
                            LabelListItem(
                                label = label,
                                usageCount = labelUsageCounts[label.id] ?: 0,
                                onEditClick = { editingLabel = label },
                                onDeleteClick = { labelToDelete = label },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
            
            // Snackbar host
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
    
    // Create label dialog
    if (showCreateDialog) {
        CreateEditLabelDialog(
            onDismiss = { showCreateDialog = false },
            onSave = { newLabel ->
                coroutineScope.launch {
                    // Create label through HabitManager
                    val result = habitManager.createLabel(newLabel)
                    if (result is LabelResult.Success) {
                        refreshLabels()
                        showCreateDialog = false
                        snackbarHostState.showSnackbar(
                            message = "Label created successfully",
                            duration = SnackbarDuration.Short
                        )
                    } else if (result is LabelResult.Error) {
                        // Show error message
                        snackbarHostState.showSnackbar(
                            message = result.message,
                            duration = SnackbarDuration.Long
                        )
                        // Dialog stays open so user can fix and retry
                    }
                }
            }
        )
    }
    
    // Edit label dialog
    editingLabel?.let { label ->
        CreateEditLabelDialog(
            label = label,
            onDismiss = { editingLabel = null },
            onSave = { updatedLabel ->
                coroutineScope.launch {
                    // Update label through HabitManager
                    val result = habitManager.updateLabel(updatedLabel)
                    if (result is LabelResult.Success) {
                        refreshLabels()
                        editingLabel = null
                        snackbarHostState.showSnackbar(
                            message = "Label updated successfully",
                            duration = SnackbarDuration.Short
                        )
                    } else if (result is LabelResult.Error) {
                        // Show error message
                        snackbarHostState.showSnackbar(
                            message = result.message,
                            duration = SnackbarDuration.Long
                        )
                        // Dialog stays open
                    }
                }
            }
        )
    }
    
    // Delete confirmation dialog
    labelToDelete?.let { label ->
        DeleteLabelDialog(
            label = label,
            usageCount = labelUsageCounts[label.id] ?: 0,
            onDismiss = { labelToDelete = null },
            onConfirm = {
                coroutineScope.launch {
                    try {
                        // Delete label through HabitManager
                        habitManager.deleteLabel(label)
                        refreshLabels()
                        labelToDelete = null
                        snackbarHostState.showSnackbar(
                            message = "Label deleted successfully",
                            duration = SnackbarDuration.Short
                        )
                    } catch (e: Exception) {
                        Log.e("LabelManagementScreen", "Failed to delete label: ${e.message}", e)
                        snackbarHostState.showSnackbar(
                            message = "Failed to delete label: ${e.message}",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun LabelListItem(
    label: LabelEntity,
    usageCount: Int,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppSpacing.standardSpacing),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label chip
            LabelChip(
                label = label.toCore(),
                modifier = Modifier.weight(1f)
            )
            
            // Usage count
            Text(
                text = "Used by $usageCount ${if (usageCount == 1) "habit" else "habits"}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(horizontal = AppSpacing.smallSpacing)
            )
            
            // Edit button
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, "Edit")
            }
            
            // Delete button
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

@Composable
fun EmptyLabelsState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No Labels Yet",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        Text(
            text = "Create labels to organize your habits",
            style = MaterialTheme.typography.body2,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = AppSpacing.standardSpacing),
            textAlign = TextAlign.Center
        )
        Button(onClick = onCreateClick) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create First Label")
        }
    }
}

