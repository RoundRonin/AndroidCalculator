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

internal enum class BinaryOperation(
    val symbol: String,
) {
    Add("+") {
        override fun apply(left: Double, right: Double) = left + right
    },
    Subtract("−") {
        override fun apply(left: Double, right: Double) = left - right
    },
    Multiply("×") {
        override fun apply(left: Double, right: Double) = left * right
    },
    Divide("÷") {
        override fun apply(left: Double, right: Double) = left / right
    },
    ;

    abstract fun apply(left: Double, right: Double): Double
}
