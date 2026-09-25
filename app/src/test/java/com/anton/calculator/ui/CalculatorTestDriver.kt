package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import androidx.lifecycle.SavedStateHandle

internal fun calculatorViewModel(decimalSeparator: Char = '.'): CalculatorViewModel =
    CalculatorViewModel(SavedStateHandle()).apply {
        onDecimalSeparatorChanged(decimalSeparator)
    }

internal fun calculate(
    left: String,
    operation: BinaryOperation,
    right: String,
    decimalSeparator: Char = '.',
): CalculatorViewModel = calculatorViewModel(decimalSeparator).apply {
    enter(left)
    onAction(CalculatorAction.SelectOperation(operation))
    enter(right)
    onAction(CalculatorAction.Equals)
}

internal fun errorViewModel(decimalSeparator: Char = '.'): CalculatorViewModel =
    calculate("1", BinaryOperation.Divide, "0", decimalSeparator)

internal fun CalculatorViewModel.enter(value: String) {
    value.forEach { character ->
        when {
            character.isDigit() -> onAction(CalculatorAction.Digit(character.digitToInt()))
            character == uiState.value.decimalSeparator -> onAction(CalculatorAction.Decimal)
            else -> error("Unsupported calculator test input: $character")
        }
    }
}

