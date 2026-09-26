package com.anton.calculator.ui

import com.anton.calculator.domain.CalculationExpression
import com.anton.calculator.domain.CalculatorState
import com.anton.calculator.domain.PendingCalculation
import com.anton.calculator.domain.toCalculatorText

internal fun CalculatorState.toUiState(decimalSeparator: Char): CalculatorUiState = when (this) {
    is CalculatorState.EnteringFirstOperand -> CalculatorUiState(
        primaryValue = entry.withDecimalSeparator(decimalSeparator),
        decimalSeparator = decimalSeparator,
    )
    is CalculatorState.OperationPending -> CalculatorUiState(
        expression = calculation.toUiExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        selectedOperation = calculation.operation,
    )
    is CalculatorState.EnteringSecondOperand -> CalculatorUiState(
        primaryValue = entry.withDecimalSeparator(decimalSeparator),
        expression = calculation.toUiExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        selectedOperation = calculation.operation,
    )
    is CalculatorState.Result -> CalculatorUiState(
        primaryValue = value.toCalculatorText().withDecimalSeparator(decimalSeparator),
        expression = expression.toUiExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        displayStatus = CalculatorDisplayStatus.Result,
    )
    is CalculatorState.Error -> CalculatorUiState(
        primaryValue = null,
        expression = expression.toUiExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        displayStatus = CalculatorDisplayStatus.Error,
    )
}

private fun PendingCalculation.toUiExpression(decimalSeparator: Char) = CalculatorUiExpression(
    leftOperand = operandText.withDecimalSeparator(decimalSeparator),
    operation = operation,
)

private fun CalculationExpression.toUiExpression(decimalSeparator: Char) = CalculatorUiExpression(
    leftOperand = leftOperandText.withDecimalSeparator(decimalSeparator),
    operation = operation,
    rightOperand = rightOperandText.withDecimalSeparator(decimalSeparator),
)

private fun String.withDecimalSeparator(decimalSeparator: Char): String =
    if (decimalSeparator == '.') this else replace('.', decimalSeparator)
