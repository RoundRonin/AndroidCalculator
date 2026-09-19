package com.anton.calculator

sealed interface CalculatorAction {
    data class Digit(val value: Int) : CalculatorAction {
        init {
            require(value in 0..9) { "A calculator digit must be between 0 and 9." }
        }
    }

    data object ClearAll : CalculatorAction
}
