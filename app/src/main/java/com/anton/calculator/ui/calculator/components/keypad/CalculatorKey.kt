package com.anton.calculator.ui.calculator.components.keypad

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun RowScope.CalculatorKey(
    label: String,
    contentDescription: String?,
    selected: Boolean?,
    containerColor: Color,
    contentColor: Color,
    shape: Shape,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) PRESSED_SCALE else 1f,
        animationSpec = spring(stiffness = 700f, dampingRatio = 0.8f),
    )
    val hapticFeedback = LocalHapticFeedback.current

    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            onClick()
        },
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
            .semantics {
                contentDescription?.let { this.contentDescription = it }
                selected?.let { this.selected = it }
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        interactionSource = interactionSource,
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        border = selected
            ?.takeIf { it }
            ?.let { BorderStroke(width = 2.dp, color = contentColor) },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 14.sp,
                maxFontSize = 22.sp,
                stepSize = 1.sp,
            ),
            maxLines = 1,
        )
    }
}

private const val PRESSED_SCALE = 0.96f
