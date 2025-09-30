package com.example.rewire.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.example.rewire.ui.theme.AppShapes
import com.example.rewire.ui.theme.AppSpacing
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(AppSpacing.cardPadding)
            .clickable { onClick() },
        shape = AppShapes.cardShape,
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(AppSpacing.standardRowHeight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete",
                modifier = Modifier.size(32.dp),
                tint = Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeleteButtonPreview() {
    MaterialTheme {
        DeleteButton(
            onClick = { /* Preview action */ }
        )
    }
}
