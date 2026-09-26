package com.anton.calculator.ui.components

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
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
