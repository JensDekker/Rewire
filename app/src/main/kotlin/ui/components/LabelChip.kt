package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.rewire.core.Label
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelChip(
    label: Label,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val labelColor = try {
        Color(android.graphics.Color.parseColor(label.color))
    } catch (e: Exception) {
        MaterialTheme.colors.primary  // Fallback color
    }
    
    // Calculate text color based on background brightness
    val textColor = if (isColorDark(labelColor)) Color.White else Color.Black
    
    Box(
        modifier = modifier
            .background(labelColor, RoundedCornerShape(16.dp))
            .pointerInput(label.id) {
                detectTapGestures(
                    onLongPress = {
                        onLongClick?.invoke()
                    }
                )
            }
            .clickable(onClick = onClick)
            .padding(horizontal = AppSpacing.smallSpacing, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label.name,
            color = textColor,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Helper function to determine if a color is dark
private fun isColorDark(color: Color): Boolean {
    val brightness = (color.red * 299 + color.green * 587 + color.blue * 114) / 1000
    return brightness < 0.5
}

