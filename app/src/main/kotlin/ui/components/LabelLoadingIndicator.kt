package com.example.rewire.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rewire.ui.theme.AppColors
import com.example.rewire.ui.theme.AppSpacing
import com.example.rewire.ui.theme.AppShapes

@Composable
fun LabelLoadingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AppSpacing.smallSpacing)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(24.dp)
                    .background(
                        AppColors.surfaceVariant,
                        AppShapes.cardShape
                    )
            )
        }
    }
}

