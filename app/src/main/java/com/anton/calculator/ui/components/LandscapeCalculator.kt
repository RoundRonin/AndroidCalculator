package com.anton.calculator.ui.components

import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorUiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun LandscapeCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalculatorDisplay(
            state = state,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
        )
        CalculatorKeypad(
            decimalSeparator = state.decimalSeparator,
            selectedOperation = state.selectedOperation,
            onAction = onAction,
            keyShape = RoundedCornerShape(percent = 50),
            squareKeys = false,
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f),
        )
    }
}
