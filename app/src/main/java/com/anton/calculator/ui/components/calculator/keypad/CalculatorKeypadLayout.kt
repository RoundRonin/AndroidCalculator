package com.anton.calculator.ui.components.calculator.keypad

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

internal val keypadRows = listOf(
    listOf(
        CalculatorAction.ClearAll,
        CalculatorAction.ClearEntry,
        null,
        CalculatorAction.SelectOperation(BinaryOperation.Divide),
    ),
    listOf(
        CalculatorAction.Digit(7),
        CalculatorAction.Digit(8),
        CalculatorAction.Digit(9),
        CalculatorAction.SelectOperation(BinaryOperation.Multiply),
    ),
    listOf(
        CalculatorAction.Digit(4),
        CalculatorAction.Digit(5),
        CalculatorAction.Digit(6),
        CalculatorAction.SelectOperation(BinaryOperation.Subtract),
    ),
    listOf(
        CalculatorAction.Digit(1),
        CalculatorAction.Digit(2),
        CalculatorAction.Digit(3),
        CalculatorAction.SelectOperation(BinaryOperation.Add),
    ),
    listOf(
        CalculatorAction.Decimal,
        CalculatorAction.Digit(0),
        null,
        CalculatorAction.Equals,
    ),
)
