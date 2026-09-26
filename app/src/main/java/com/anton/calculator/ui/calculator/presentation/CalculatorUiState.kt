package com.anton.calculator.ui.calculator.presentation

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.state.CalculatorState

internal data class CalculatorUiState(
    val primaryValue: String? = CalculatorState.ZERO,
    val expression: CalculatorUiExpression? = null,
    val decimalSeparator: Char = '.',
    val displayStatus: CalculatorDisplayStatus = CalculatorDisplayStatus.Editing,
    val selectedOperation: BinaryOperation? = null,
)
