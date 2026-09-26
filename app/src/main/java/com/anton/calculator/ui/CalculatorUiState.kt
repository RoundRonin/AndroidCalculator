package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorState

internal data class CalculatorUiState(
    val primaryValue: String? = CalculatorState.ZERO,
    val expression: CalculatorUiExpression? = null,
    val decimalSeparator: Char = '.',
    val displayStatus: CalculatorDisplayStatus = CalculatorDisplayStatus.Editing,
    val selectedOperation: BinaryOperation? = null,
)
