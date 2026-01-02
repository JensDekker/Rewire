package com.example.rewire.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography

@Composable
fun DeleteLabelDialog(
    label: LabelEntity,
    usageCount: Int,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Label") },
        text = {
            Column {
                Text("Are you sure you want to delete the label \"${label.name}\"?")
                Spacer(modifier = Modifier.height(AppSpacing.smallSpacing))
                if (usageCount > 0) {
                    Text(
                        text = "This label is currently used by $usageCount ${if (usageCount == 1) "habit" else "habits"}. " +
                                "The label will be removed from all habits.",
                        color = AppColors.error,
                        style = AppTypography.materialTypography.body2
                    )
                } else {
                    Text(
                        text = "This label is not used by any habits.",
                        color = AppColors.textSecondary,
                        style = AppTypography.materialTypography.body2
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = AppColors.error)
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

