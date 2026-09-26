package com.anton.calculator.ui.calculator.components.layout

import com.anton.calculator.domain.calculation.CalculatorAction
import com.anton.calculator.ui.calculator.components.display.CalculatorDisplay
import com.anton.calculator.ui.calculator.components.keypad.CalculatorKeypad
import com.anton.calculator.ui.calculator.components.keypad.CalculatorKeypadLayout
import com.anton.calculator.ui.calculator.presentation.CalculatorUiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CalculatorDisplay(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(2f),
        )
        CalculatorKeypad(
            decimalSeparator = state.decimalSeparator,
            selectedOperation = state.selectedOperation,
            onAction = onAction,
            layout = CalculatorKeypadLayout.CompactHeight,
            modifier = Modifier
                .fillMaxWidth()
                .weight(4f),
        )
    }
}
