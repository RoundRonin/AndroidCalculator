package com.anton.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom,
            ) {
                if (state.secondaryExpression.isNotEmpty()) {
                    Text(
                        text = state.secondaryExpression,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.End,
                    )
                }
                Text(
                    text = state.primaryValue,
                    style = MaterialTheme.typography.displayLarge,
                    textAlign = TextAlign.End,
                )
            }

            Keypad(onAction = onAction)
        }
    }
}

@Composable
private fun Keypad(onAction: (CalculatorAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        keypadRows.forEach { cells ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                cells.forEach { cell ->
                    when (cell) {
                        is KeypadCell.Digit -> CalculatorKey(
                            label = cell.value.toString(),
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface,
                            onClick = { onAction(CalculatorAction.Digit(cell.value)) },
                        )
                        KeypadCell.ClearAll -> CalculatorKey(
                            label = stringResource(R.string.clear_all_label),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = { onAction(CalculatorAction.ClearAll) },
                        )
                        KeypadCell.ClearEntry -> CalculatorKey(
                            label = stringResource(R.string.clear_entry_label),
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            onClick = { onAction(CalculatorAction.ClearEntry) },
                        )
                        is KeypadCell.Operation -> CalculatorKey(
                            label = cell.operation.symbol,
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            onClick = {
                                onAction(CalculatorAction.SelectOperation(cell.operation))
                            },
                        )
                        KeypadCell.Equals -> CalculatorKey(
                            label = "=",
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            onClick = { onAction(CalculatorAction.Equals) },
                        )
                        KeypadCell.Empty -> Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.CalculatorKey(
    label: String,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .weight(1f)
            .heightIn(min = 64.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}

private sealed interface KeypadCell {
    data class Digit(val value: Int) : KeypadCell
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
        KeypadCell.Empty,
        KeypadCell.Digit(0),
        KeypadCell.Empty,
        KeypadCell.Equals,
    ),
)
