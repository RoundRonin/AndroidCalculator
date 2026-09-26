package com.anton.calculator.ui.calculator.components.keypad

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.CalculatorAction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Composable
internal fun ColumnScope.KeypadRow(
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
                val presentation = action.toKeyPresentation(decimalSeparator, selectedOperation)
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
