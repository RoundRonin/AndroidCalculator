package com.anton.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.SelectionState
import androidx.compose.foundation.text.selection.rememberSelectionState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
    primarySelectionState: SelectionState = rememberSelectionState(),
) {
    Surface(modifier = modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val useCompactHeightLayout = maxHeight < COMPACT_HEIGHT_BREAKPOINT && maxWidth > maxHeight
            if (useCompactHeightLayout) {
                LandscapeCalculator(
                    state = state,
                    onAction = onAction,
                    primarySelectionState = primarySelectionState,
                    modifier = Modifier
                        .widthIn(max = MAX_CONTENT_WIDTH)
                        .fillMaxSize()
                        .testTag(LANDSCAPE_LAYOUT_TAG),
                )
            } else {
                PortraitCalculator(
                    state = state,
                    onAction = onAction,
                    primarySelectionState = primarySelectionState,
                    modifier = Modifier
                        .widthIn(max = MAX_CONTENT_WIDTH)
                        .fillMaxSize()
                        .testTag(PORTRAIT_LAYOUT_TAG),
                )
            }
        }
    }
}

@Composable
private fun PortraitCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    primarySelectionState: SelectionState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CalculatorDisplay(
            state = state,
            primarySelectionState = primarySelectionState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        Keypad(
            decimalSeparator = state.decimalSeparator,
            onAction = onAction,
            keyShape = CircleShape,
            squareKeys = true,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = MAX_PORTRAIT_KEYPAD_WIDTH),
        )
    }
}

@Composable
private fun LandscapeCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    primarySelectionState: SelectionState,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalculatorDisplay(
            state = state,
            primarySelectionState = primarySelectionState,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
        )
        Keypad(
            decimalSeparator = state.decimalSeparator,
            onAction = onAction,
            keyShape = RoundedCornerShape(percent = 50),
            squareKeys = false,
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f),
        )
    }
}

@Composable
private fun CalculatorDisplay(
    state: CalculatorUiState,
    primarySelectionState: SelectionState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.testTag(DISPLAY_REGION_TAG),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (state.secondaryExpression.isNotEmpty()) {
            Text(
                text = state.secondaryExpression,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SECONDARY_DISPLAY_TAG),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 14.sp,
                    maxFontSize = 22.sp,
                    stepSize = 1.sp,
                ),
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
            )
        }
        SelectionContainer(
            state = primarySelectionState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = state.primaryValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(PRIMARY_DISPLAY_TAG),
                style = MaterialTheme.typography.displayLarge,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 18.sp,
                    maxFontSize = 64.sp,
                    stepSize = 2.sp,
                ),
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun Keypad(
    decimalSeparator: Char,
    onAction: (CalculatorAction) -> Unit,
    keyShape: Shape,
    squareKeys: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.testTag(KEYPAD_TAG),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        keypadRows.forEach { cells ->
            KeypadRow(
                cells = cells,
                decimalSeparator = decimalSeparator,
                onAction = onAction,
                keyShape = keyShape,
                squareKeys = squareKeys,
            )
        }
    }
}

@Composable
private fun ColumnScope.KeypadRow(
    cells: List<KeypadCell>,
    decimalSeparator: Char,
    onAction: (CalculatorAction) -> Unit,
    keyShape: Shape,
    squareKeys: Boolean,
) {
    Row(
        modifier = if (squareKeys) {
            Modifier.fillMaxWidth()
        } else {
            Modifier
                .fillMaxWidth()
                .weight(1f)
        },
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        cells.forEach { cell ->
            val cellModifier = if (squareKeys) {
                Modifier
                    .weight(1f)
                    .aspectRatio(1f)
            } else {
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            }
            when (cell) {
                is KeypadCell.Digit -> CalculatorKey(
                    label = cell.value.toString(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(CalculatorAction.Digit(cell.value)) },
                )
                KeypadCell.Decimal -> CalculatorKey(
                    label = decimalSeparator.toString(),
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(CalculatorAction.Decimal) },
                )
                KeypadCell.ClearAll -> CalculatorKey(
                    label = stringResource(R.string.clear_all_label),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(CalculatorAction.ClearAll) },
                )
                KeypadCell.ClearEntry -> CalculatorKey(
                    label = stringResource(R.string.clear_entry_label),
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(CalculatorAction.ClearEntry) },
                )
                is KeypadCell.Operation -> CalculatorKey(
                    label = cell.operation.symbol,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = {
                        onAction(CalculatorAction.SelectOperation(cell.operation))
                    },
                )
                KeypadCell.Equals -> CalculatorKey(
                    label = "=",
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(CalculatorAction.Equals) },
                )
                KeypadCell.Empty -> Spacer(modifier = cellModifier)
            }
        }
    }
}

@Composable
private fun RowScope.CalculatorKey(
    label: String,
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
        label = "calculator key press",
    )
    val hapticFeedback = LocalHapticFeedback.current
    Button(
        onClick = {
            hapticFeedback.performHapticFeedback(HapticFeedbackType.VirtualKey)
            onClick()
        },
        modifier = modifier
            .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
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

private sealed interface KeypadCell {
    data class Digit(val value: Int) : KeypadCell
    data object Decimal : KeypadCell
    data class Operation(val operation: BinaryOperation) : KeypadCell
    data object ClearAll : KeypadCell
    data object ClearEntry : KeypadCell
    data object Equals : KeypadCell
    data object Empty : KeypadCell
}

private val keypadRows = listOf(
    listOf(
        KeypadCell.ClearAll,
        KeypadCell.ClearEntry,
        KeypadCell.Empty,
        KeypadCell.Operation(BinaryOperation.Divide),
    ),
    listOf(
        KeypadCell.Digit(7),
        KeypadCell.Digit(8),
        KeypadCell.Digit(9),
        KeypadCell.Operation(BinaryOperation.Multiply),
    ),
    listOf(
        KeypadCell.Digit(4),
        KeypadCell.Digit(5),
        KeypadCell.Digit(6),
        KeypadCell.Operation(BinaryOperation.Subtract),
    ),
    listOf(
        KeypadCell.Digit(1),
        KeypadCell.Digit(2),
        KeypadCell.Digit(3),
        KeypadCell.Operation(BinaryOperation.Add),
    ),
    listOf(
        KeypadCell.Decimal,
        KeypadCell.Digit(0),
        KeypadCell.Empty,
        KeypadCell.Equals,
    ),
)

internal const val PRIMARY_DISPLAY_TAG = "primaryDisplay"
internal const val SECONDARY_DISPLAY_TAG = "secondaryDisplay"
internal const val PORTRAIT_LAYOUT_TAG = "portraitLayout"
internal const val LANDSCAPE_LAYOUT_TAG = "landscapeLayout"
internal const val DISPLAY_REGION_TAG = "displayRegion"
internal const val KEYPAD_TAG = "keypad"

private val COMPACT_HEIGHT_BREAKPOINT = 480.dp
private val MAX_CONTENT_WIDTH = 520.dp
private val MAX_PORTRAIT_KEYPAD_WIDTH = 400.dp
private const val PRESSED_SCALE = 0.96f
