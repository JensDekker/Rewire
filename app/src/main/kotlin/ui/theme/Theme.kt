package com.example.rewire.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorPalette = lightColors(
    primary = Color(0xFF6200EE),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    error = Color(0xFFB00020),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onError = Color.White
)

/**
 * Material shapes aligned with [AppShapes] for components that read theme shapes.
 * Only `small` is remapped (OutlinedTextField / TextField); medium/large stay default
 * so cards and buttons are not redesigned in this pass.
 */
private val AppMaterialShapes = Shapes(
    small = AppShapes.inputShape
)

@Composable
fun RewireTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = LightColorPalette,
        typography = androidx.compose.material.Typography(),
        shapes = AppMaterialShapes,
        content = content
    )
}
