package com.anton.calculator.domain.calculation.state

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
