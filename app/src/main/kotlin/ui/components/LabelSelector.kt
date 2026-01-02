package com.example.rewire.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.rewire.core.Label
import com.example.rewire.db.entity.LabelEntity
import com.example.rewire.db.entity.toCore
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography

@Composable
fun LabelSelector(
    allLabels: List<LabelEntity>,
    selectedLabelIds: Set<Long>,
    onSelectionChange: (Set<Long>) -> Unit,
    showTitle: Boolean = true,
    navController: NavController? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (showTitle) {
            Text(
                text = "Labels",
                style = AppTypography.materialTypography.subtitle1,
                modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
            )
        }
        
        if (allLabels.isEmpty()) {
            // Empty state
            Text(
                text = "No labels yet. Create your first label to organize habits.",
                style = AppTypography.materialTypography.body2,
                color = AppColors.textSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = AppSpacing.standardSpacing),
                textAlign = TextAlign.Center
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
                contentPadding = PaddingValues(vertical = AppSpacing.smallSpacing)
            ) {
                items(allLabels) { labelEntity ->
                    val label = labelEntity.toCore()
                    val isSelected = selectedLabelIds.contains(label.id)
                    
                    LabelChip(
                        label = label,
                        onClick = {
                            val newSelection = if (isSelected) {
                                selectedLabelIds - label.id
                            } else {
                                selectedLabelIds + label.id
                            }
                            onSelectionChange(newSelection)
                        },
                        onLongClick = navController?.let { { 
                            navController.navigate("label_management")
                        } },
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

