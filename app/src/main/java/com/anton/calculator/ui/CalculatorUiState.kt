package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculationExpression
import com.anton.calculator.domain.CalculatorState
import com.anton.calculator.domain.PendingCalculation
import com.anton.calculator.domain.toCalculatorText

internal data class CalculatorUiState(
    val primaryValue: String = "0",
    val secondaryExpression: String = "",
    val decimalSeparator: Char = '.',
    val displayStatus: CalculatorDisplayStatus = CalculatorDisplayStatus.Editing,
    val selectedOperation: BinaryOperation? = null,
)

internal enum class CalculatorDisplayStatus {
    Editing,
    Result,
    Error,
}

internal fun CalculatorState.toUiState(decimalSeparator: Char): CalculatorUiState = when (this) {
    is CalculatorState.EnteringFirstOperand -> CalculatorUiState(
        primaryValue = entry.withDecimalSeparator(decimalSeparator),
        decimalSeparator = decimalSeparator,
    )
    is CalculatorState.OperationPending -> CalculatorUiState(
        secondaryExpression = calculation.pendingExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        selectedOperation = calculation.operation,
    )
    is CalculatorState.EnteringSecondOperand -> CalculatorUiState(
        primaryValue = entry.withDecimalSeparator(decimalSeparator),
        secondaryExpression = calculation.pendingExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        selectedOperation = calculation.operation,
    )
    is CalculatorState.Result -> CalculatorUiState(
        primaryValue = value.toCalculatorText().withDecimalSeparator(decimalSeparator),
        secondaryExpression = expression.completedExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        displayStatus = CalculatorDisplayStatus.Result,
    )
    is CalculatorState.Error -> CalculatorUiState(
        primaryValue = ERROR_TEXT,
        secondaryExpression = expression.completedExpression(decimalSeparator),
        decimalSeparator = decimalSeparator,
        displayStatus = CalculatorDisplayStatus.Error,
    )
}

private fun PendingCalculation.pendingExpression(decimalSeparator: Char): String =
    "${operandText.withDecimalSeparator(decimalSeparator)} ${operation.symbol}"

private fun CalculationExpression.completedExpression(decimalSeparator: Char): String =
    "${leftOperandText.withDecimalSeparator(decimalSeparator)} ${operation.symbol} " +
        "${rightOperandText.withDecimalSeparator(decimalSeparator)} ="

private fun String.withDecimalSeparator(decimalSeparator: Char): String =
    if (decimalSeparator == '.') this else replace('.', decimalSeparator)

private const val ERROR_TEXT = "Error"
