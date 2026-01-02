package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppTypography

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    onCustomColorClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Select Color",
            style = AppTypography.materialTypography.subtitle2,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        // Color grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),  // 5 columns for 5-color palette
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        ) {
            items(AppColors.labelDefaultColors) { color ->
                val isSelected = color == selectedColor
                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .background(
                            Color(android.graphics.Color.parseColor(color)),
                            CircleShape
                        )
                        .border(
                            if (isSelected) 3.dp else 1.dp,
                            if (isSelected) AppColors.primary 
                            else AppColors.borderMedium,
                            CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
        
        // Custom color button
        if (onCustomColorClick != null) {
            TextButton(onClick = onCustomColorClick) {
                Text("Custom Color")
            }
        }
    }
}

