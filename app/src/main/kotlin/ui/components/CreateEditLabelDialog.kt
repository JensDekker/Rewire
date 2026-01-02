package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import com.example.rewire.ui.components.ColorPicker
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography
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
        mutableStateOf(label?.color ?: AppColors.getDefaultColorForNewLabel(0))
    }
    var nameError by remember { mutableStateOf(false) }
    var nameErrorMessage by remember { mutableStateOf("") }
    var colorError by remember { mutableStateOf(false) }
    var colorErrorMessage by remember { mutableStateOf("") }
    var showColorPicker by remember { mutableStateOf(false) }
    var showCustomColorInput by remember { mutableStateOf(false) }
    var customColorHex by remember { mutableStateOf("") }
    var hexInputError by remember { mutableStateOf(false) }
    var hexInputErrorMessage by remember { mutableStateOf("") }
    
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
                        color = AppColors.error,
                        style = AppTypography.materialTypography.caption,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(AppSpacing.standardSpacing))
                
                Text(
                    text = "Color",
                    style = AppTypography.materialTypography.subtitle2,
                    modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
                )
                
                // Color picker or color display
                if (showColorPicker) {
                    Column {
                        if (showCustomColorInput) {
                            // Custom hex color input
                            OutlinedTextField(
                                value = customColorHex,
                                onValueChange = { newValue ->
                                    customColorHex = newValue
                                    hexInputError = false
                                    hexInputErrorMessage = ""
                                    colorError = false
                                    colorErrorMessage = ""
                                    
                                    // Validate hex format
                                    if (newValue.isNotEmpty()) {
                                        val hexColorPattern = Regex("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{8})$")
                                        if (newValue.matches(hexColorPattern)) {
                                            // Valid hex color - apply it
                                            labelColor = newValue.uppercase()
                                            showCustomColorInput = false
                                            customColorHex = ""
                                        } else if (newValue.length > 1 && newValue.startsWith("#")) {
                                            // Check if it's a partial valid hex (user is still typing)
                                            val partialHexPattern = Regex("^#[A-Fa-f0-9]{0,8}$")
                                            if (!newValue.matches(partialHexPattern)) {
                                                hexInputError = true
                                                hexInputErrorMessage = "Invalid hex format. Use format like #FFB3BA"
                                            }
                                        } else if (newValue.isNotEmpty() && !newValue.startsWith("#")) {
                                            hexInputError = true
                                            hexInputErrorMessage = "Hex color must start with #"
                                        }
                                    }
                                },
                                label = { Text("Hex Color (e.g., #FFB3BA)") },
                                placeholder = { Text("#FFB3BA") },
                                isError = hexInputError || (colorError && customColorHex.isNotEmpty()),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            if ((hexInputError && hexInputErrorMessage.isNotEmpty()) || (colorError && customColorHex.isNotEmpty() && colorErrorMessage.isNotEmpty())) {
                                Text(
                                    text = hexInputErrorMessage.ifEmpty { colorErrorMessage },
                                    color = AppColors.error,
                                    style = AppTypography.materialTypography.caption,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
                            ) {
                                TextButton(onClick = { 
                                    showCustomColorInput = false
                                    customColorHex = ""
                                    hexInputError = false
                                    hexInputErrorMessage = ""
                                    colorError = false
                                    colorErrorMessage = ""
                                }) {
                                    Text("Cancel")
                                }
                                TextButton(onClick = { 
                                    // Validate and apply current hex value
                                    if (customColorHex.isNotEmpty()) {
                                        val colorValidation = validateLabelColor(customColorHex)
                                        if (colorValidation.isValid) {
                                            labelColor = customColorHex.uppercase()
                                            showCustomColorInput = false
                                            customColorHex = ""
                                        } else {
                                            hexInputError = true
                                            hexInputErrorMessage = colorValidation.errorMessage
                                        }
                                    }
                                }) {
                                    Text("Apply")
                                }
                            }
                        } else {
                            ColorPicker(
                                selectedColor = labelColor,
                                onColorSelected = { 
                                    labelColor = it
                                    showColorPicker = false
                                },
                                onCustomColorClick = {
                                    showCustomColorInput = true
                                    customColorHex = labelColor
                                    hexInputError = false
                                    hexInputErrorMessage = ""
                                },
                                modifier = Modifier.padding(top = AppSpacing.smallSpacing)
                            )
                        }
                    }
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
                                        AppColors.primary
                                    },
                                    AppShapes.smallCardShape
                                )
                                .border(1.dp, AppColors.borderMedium, AppShapes.smallCardShape)
                        )
                        Text(labelColor)
                        TextButton(onClick = { 
                            showColorPicker = true
                            // Reset custom color input state when reopening picker
                            showCustomColorInput = false
                            customColorHex = ""
                            hexInputError = false
                            hexInputErrorMessage = ""
                        }) {
                            Text("Change Color")
                        }
                    }
                }
                if (colorError) {
                    Text(
                        text = colorErrorMessage,
                        color = AppColors.error,
                        style = AppTypography.materialTypography.caption,
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

