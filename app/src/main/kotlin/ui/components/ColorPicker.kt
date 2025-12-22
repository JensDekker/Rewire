package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.LabelColors

@Composable
fun ColorPicker(
    selectedColor: String,
    onColorSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Select Color",
            style = MaterialTheme.typography.subtitle2,
            modifier = Modifier.padding(bottom = AppSpacing.smallSpacing)
        )
        
        // Color grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),  // 6 columns
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
            verticalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
        ) {
            items(LabelColors.DEFAULT_COLORS) { color ->
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
                            if (isSelected) MaterialTheme.colors.primary 
                            else MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            CircleShape
                        )
                        .clickable { onColorSelected(color) }
                )
            }
        }
    }
}

