package com.anton.calculator.ui.calculator.components.keypad

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.CalculatorAction
import com.anton.calculator.ui.calculator.components.KEYPAD_TAG

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
internal fun CalculatorKeypad(
    decimalSeparator: Char,
    selectedOperation: BinaryOperation?,
    onAction: (CalculatorAction) -> Unit,
    layout: CalculatorKeypadLayout,
    modifier: Modifier = Modifier,
) {
    val squareKeys = layout == CalculatorKeypadLayout.Portrait
    val keyShape = if (squareKeys) CircleShape else MaterialTheme.shapes.large
    val spacing = if (squareKeys) 8.dp else 4.dp

    Column(
        modifier = modifier.testTag(KEYPAD_TAG),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        layout.rows.forEach { cells ->
            KeypadRow(
                cells = cells,
                decimalSeparator = decimalSeparator,
                selectedOperation = selectedOperation,
                onAction = onAction,
                keyShape = keyShape,
                squareKeys = squareKeys,
                spacing = spacing,
            )
        }
    }
}
