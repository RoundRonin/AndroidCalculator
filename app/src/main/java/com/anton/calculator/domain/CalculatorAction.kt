package com.anton.calculator.domain

internal sealed interface CalculatorAction {
    data class Digit(val value: Int) : CalculatorAction {
        init {
            require(value in 0..9) { "A calculator digit must be between 0 and 9." }
        }
    }

    data object Decimal : CalculatorAction
    data class SelectOperation(val operation: BinaryOperation) : CalculatorAction
    data object Equals : CalculatorAction
    data object ClearEntry : CalculatorAction
    data object ClearAll : CalculatorAction
}
