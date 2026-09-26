package com.anton.calculator.ui.components

import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorUiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun PortraitCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CalculatorDisplay(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        CalculatorKeypad(
            decimalSeparator = state.decimalSeparator,
            selectedOperation = state.selectedOperation,
            onAction = onAction,
            keyShape = CircleShape,
            squareKeys = true,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = MaxPortraitKeypadWidth),
        )
    }
}
