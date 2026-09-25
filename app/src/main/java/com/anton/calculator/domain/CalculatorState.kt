package com.anton.calculator.domain

internal sealed interface CalculatorState {
    data class EnteringFirstOperand(val entry: String = ZERO) : CalculatorState

    data class OperationPending(val calculation: PendingCalculation) : CalculatorState

    data class EnteringSecondOperand(
        val calculation: PendingCalculation,
        val entry: String = ZERO,
    ) : CalculatorState

    data class Result(
        val value: Double,
        val expression: CalculationExpression,
    ) : CalculatorState

    data class Error(val expression: CalculationExpression) : CalculatorState

    companion object {
        const val ZERO = "0"

        fun initial(): CalculatorState = EnteringFirstOperand()
    }
}

internal data class PendingCalculation(
    val operand: Double,
    val operandText: String,
    val operation: BinaryOperation,
)

internal data class CalculationExpression(
    val leftOperandText: String,
    val operation: BinaryOperation,
    val rightOperandText: String,
)
