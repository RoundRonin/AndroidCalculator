package com.anton.calculator.ui.calculator.components.keypad

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.CalculatorAction

internal enum class CalculatorKeypadLayout {
    Portrait,
    CompactHeight,
}

internal val CalculatorKeypadLayout.rows: List<List<KeypadCell>>
    get() = when (this) {
        CalculatorKeypadLayout.Portrait -> portraitRows
        CalculatorKeypadLayout.CompactHeight -> compactHeightRows
    }

private val portraitRows = listOf(
    listOf(
        CalculatorAction.ClearAll.cell(),
        CalculatorAction.ClearEntry.cell(),
        KeypadCell(),
        CalculatorAction.SelectOperation(BinaryOperation.Divide).cell(),
    ),
    listOf(
        CalculatorAction.Digit(7).cell(),
        CalculatorAction.Digit(8).cell(),
        CalculatorAction.Digit(9).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Multiply).cell(),
    ),
    listOf(
        CalculatorAction.Digit(4).cell(),
        CalculatorAction.Digit(5).cell(),
        CalculatorAction.Digit(6).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Subtract).cell(),
    ),
    listOf(
        CalculatorAction.Digit(1).cell(),
        CalculatorAction.Digit(2).cell(),
        CalculatorAction.Digit(3).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Add).cell(),
    ),
    listOf(
        CalculatorAction.Decimal.cell(),
        CalculatorAction.Digit(0).cell(),
        KeypadCell(),
        CalculatorAction.Equals.cell(),
    ),
)

private val compactHeightRows = listOf(
    listOf(
        CalculatorAction.Digit(7).cell(),
        CalculatorAction.Digit(8).cell(),
        CalculatorAction.Digit(9).cell(),
        CalculatorAction.ClearAll.cell(),
        CalculatorAction.ClearEntry.cell(),
    ),
    listOf(
        CalculatorAction.Digit(4).cell(),
        CalculatorAction.Digit(5).cell(),
        CalculatorAction.Digit(6).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Divide).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Multiply).cell(),
    ),
    listOf(
        CalculatorAction.Digit(1).cell(),
        CalculatorAction.Digit(2).cell(),
        CalculatorAction.Digit(3).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Subtract).cell(),
        CalculatorAction.SelectOperation(BinaryOperation.Add).cell(),
    ),
    listOf(
        CalculatorAction.Decimal.cell(),
        CalculatorAction.Digit(0).cell(weight = 2f),
        CalculatorAction.Equals.cell(weight = 2f),
    ),
)

private fun CalculatorAction.cell(weight: Float = 1f) = KeypadCell(
    action = this,
    weight = weight,
)
