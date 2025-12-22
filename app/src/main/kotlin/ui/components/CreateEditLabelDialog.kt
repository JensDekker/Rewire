package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.repository.validateLabelName
import com.example.rewire.repository.validateLabelColor
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.LabelColors
import com.example.rewire.ui.components.ColorPicker
import java.time.LocalDateTime

@Composable
fun CreateEditLabelDialog(
    label: LabelEntity? = null,  // null = create, non-null = edit
    onDismiss: () -> Unit,
    onSave: (LabelEntity) -> Unit
) {
    var labelName by remember(label) { 
        mutableStateOf(label?.name ?: "") 
    }
    // For new labels, use a default color. For editing, use existing color.
    var labelColor by remember(label) { 
        mutableStateOf(label?.color ?: LabelColors.getDefaultColorForNewLabel(0))
    }
    var nameError by remember { mutableStateOf(false) }
    var nameErrorMessage by remember { mutableStateOf("") }
    var colorError by remember { mutableStateOf(false) }
    var colorErrorMessage by remember { mutableStateOf("") }
    var showColorPicker by remember { mutableStateOf(false) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (label == null) "Create Label" else "Edit Label") },
        text = {
            Column {
                OutlinedTextField(
                    value = labelName,
                    onValueChange = { 
                        labelName = it
                        nameError = false
                        nameErrorMessage = ""
                    },
                    label = { Text("Label Name") },
                    isError = nameError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = nameErrorMessage,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
                
                Text(
                    text = "Color",
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
                )
                
                // Color picker or color display
                if (showColorPicker) {
                    ColorPicker(
                        selectedColor = labelColor,
                        onColorSelected = { 
                            labelColor = it
                            showColorPicker = false
                        },
                        modifier = Modifier.padding(top = AppSpacing.smallSpacing)
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    try {
                                        Color(android.graphics.Color.parseColor(labelColor))
                                    } catch (e: Exception) {
                                        MaterialTheme.colors.primary
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        )
                        Text(labelColor)
                        TextButton(onClick = { showColorPicker = true }) {
                            Text("Change Color")
                        }
                    }
                }
                if (colorError) {
                    Text(
                        text = colorErrorMessage,
                        color = MaterialTheme.colors.error,
                        style = MaterialTheme.typography.caption,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Validate
                    val nameValidation = validateLabelName(labelName)
                    val colorValidation = validateLabelColor(labelColor)
                    
                    nameError = !nameValidation.isValid
                    nameErrorMessage = nameValidation.errorMessage
                    colorError = !colorValidation.isValid
                    colorErrorMessage = colorValidation.errorMessage
                    
                    if (nameValidation.isValid && colorValidation.isValid) {
                        val labelEntity = if (label == null) {
                            // Create new
                            LabelEntity(
                                name = labelName.trim(),
                                color = labelColor,
                                createdAt = LocalDateTime.now().toString()
                            )
                        } else {
                            // Update existing
                            label.copy(
                                name = labelName.trim(),
                                color = labelColor
                            )
                        }
                        onSave(labelEntity)
                    }
                }
            ) {
                Text(if (label == null) "Create" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

