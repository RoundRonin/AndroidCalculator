package com.anton.calculator.ui.components

import com.anton.calculator.R
import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
internal fun CalculatorKeypad(
    decimalSeparator: Char,
    selectedOperation: BinaryOperation?,
    onAction: (CalculatorAction) -> Unit,
    keyShape: Shape,
    squareKeys: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.testTag(KEYPAD_TAG),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        keypadRows.forEach { actions ->
            KeypadRow(
                actions = actions,
                decimalSeparator = decimalSeparator,
                selectedOperation = selectedOperation,
                onAction = onAction,
                keyShape = keyShape,
                squareKeys = squareKeys,
            )
        }
    }
}

@Composable
private fun ColumnScope.KeypadRow(
    actions: List<CalculatorAction?>,
    decimalSeparator: Char,
    selectedOperation: BinaryOperation?,
    onAction: (CalculatorAction) -> Unit,
    keyShape: Shape,
    squareKeys: Boolean,
) {
    Row(
        modifier = if (squareKeys) Modifier.fillMaxWidth() else Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        actions.forEach { action ->
            val cellModifier = if (squareKeys) {
                Modifier.weight(1f).aspectRatio(1f)
            } else {
                Modifier.weight(1f).fillMaxHeight()
            }
            if (action == null) {
                Spacer(modifier = cellModifier)
            } else {
                val presentation = action.presentation(decimalSeparator, selectedOperation)
                CalculatorKey(
                    label = presentation.label,
                    contentDescription = presentation.contentDescription,
                    selected = presentation.selected,
                    containerColor = presentation.containerColor,
                    contentColor = presentation.contentColor,
                    shape = keyShape,
                    modifier = cellModifier,
                    onClick = { onAction(action) },
                )
            }
        }
    }
}

@Composable
private fun CalculatorAction.presentation(
    decimalSeparator: Char,
    selectedOperation: BinaryOperation?,
): KeyPresentation = when (this) {
    is CalculatorAction.Digit -> KeyPresentation(
        label = value.toString(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    CalculatorAction.Decimal -> KeyPresentation(
        label = decimalSeparator.toString(),
        contentDescription = stringResource(R.string.decimal_description, decimalSeparator),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    CalculatorAction.ClearAll -> KeyPresentation(
        label = stringResource(R.string.clear_all_label),
        contentDescription = stringResource(R.string.clear_all_description),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    CalculatorAction.ClearEntry -> KeyPresentation(
        label = stringResource(R.string.clear_entry_label),
        contentDescription = stringResource(R.string.clear_entry_description),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    is CalculatorAction.SelectOperation -> KeyPresentation(
        label = operation.symbol,
        contentDescription = stringResource(operation.descriptionResource),
        selected = operation == selectedOperation,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
    CalculatorAction.Equals -> KeyPresentation(
        label = "=",
        contentDescription = stringResource(R.string.equals_description),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
}

private data class KeyPresentation(
    val label: String,
    val containerColor: Color,
    val contentColor: Color,
    val contentDescription: String? = null,
    val selected: Boolean? = null,
)

@get:StringRes
private val BinaryOperation.descriptionResource: Int
    get() = when (this) {
        BinaryOperation.Add -> R.string.add_description
        BinaryOperation.Subtract -> R.string.subtract_description
        BinaryOperation.Multiply -> R.string.multiply_description
        BinaryOperation.Divide -> R.string.divide_description
    }

private val keypadRows = listOf(
    listOf(
        CalculatorAction.ClearAll,
        CalculatorAction.ClearEntry,
        null,
        CalculatorAction.SelectOperation(BinaryOperation.Divide),
    ),
    listOf(
        CalculatorAction.Digit(7),
        CalculatorAction.Digit(8),
        CalculatorAction.Digit(9),
        CalculatorAction.SelectOperation(BinaryOperation.Multiply),
    ),
    listOf(
        CalculatorAction.Digit(4),
        CalculatorAction.Digit(5),
        CalculatorAction.Digit(6),
        CalculatorAction.SelectOperation(BinaryOperation.Subtract),
    ),
    listOf(
        CalculatorAction.Digit(1),
        CalculatorAction.Digit(2),
        CalculatorAction.Digit(3),
        CalculatorAction.SelectOperation(BinaryOperation.Add),
    ),
    listOf(
        CalculatorAction.Decimal,
        CalculatorAction.Digit(0),
        null,
        CalculatorAction.Equals,
    ),
)
