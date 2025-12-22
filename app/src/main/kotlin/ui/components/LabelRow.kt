package com.example.rewire.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.rewire.core.Label
import com.example.rewire.ui.theme.AppSpacing

@Composable
fun LabelRow(
    labels: List<Label>,
    onLabelClick: (Label) -> Unit = {},
    onLabelLongClick: ((Label) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (labels.isEmpty()) return
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        labels.forEach { label ->
            LabelChip(
                label = label,
                onClick = { onLabelClick(label) },
                onLongClick = onLabelLongClick?.let { { it(label) } }
            )
        }
    }
}

