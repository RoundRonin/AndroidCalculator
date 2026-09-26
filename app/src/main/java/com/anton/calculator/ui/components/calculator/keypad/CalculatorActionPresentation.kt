package com.anton.calculator.ui.components.calculator.keypad

import com.anton.calculator.R
import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.components.calculator.utilities.descriptionResource
import com.anton.calculator.ui.components.calculator.utilities.labelResource

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.anton.calculator.ui.components.calculator.keypad.utilities.labelResource

@Composable
internal fun CalculatorAction.toKeyPresentation(
    decimalSeparator: Char,
    selectedOperation: BinaryOperation?,
): KeyPresentation = when (this) {
    is CalculatorAction.Digit -> KeyPresentation(
        label = stringResource(value.labelResource),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
    )
    CalculatorAction.Decimal -> KeyPresentation(
        label = stringResource(R.string.decimal_label, decimalSeparator),
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
        label = stringResource(operation.labelResource),
        contentDescription = stringResource(operation.descriptionResource),
        selected = operation == selectedOperation,
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    )
    CalculatorAction.Equals -> KeyPresentation(
        label = stringResource(R.string.equals_label),
        contentDescription = stringResource(R.string.equals_description),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    )
}
