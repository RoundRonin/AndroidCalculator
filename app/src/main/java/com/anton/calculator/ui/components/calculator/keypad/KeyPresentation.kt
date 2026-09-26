package com.anton.calculator.ui.components.calculator.keypad

import androidx.compose.ui.graphics.Color

internal data class KeyPresentation(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val contentDescription: String? = null,
    val selected: Boolean? = null,
)
