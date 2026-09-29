package com.anton.calculator.ui.calculator.components.keypad

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.CalculatorAction

internal val CalculatorAction.keyTestTag: String
    get() = when (this) {
        is CalculatorAction.Digit -> "key_digit_$value"
        CalculatorAction.Decimal -> "key_decimal"
        CalculatorAction.ClearAll -> "key_clear_all"
        CalculatorAction.ClearEntry -> "key_clear_entry"
        CalculatorAction.Equals -> "key_equals"
        is CalculatorAction.SelectOperation -> when (operation) {
            BinaryOperation.Add -> "key_add"
            BinaryOperation.Subtract -> "key_subtract"
            BinaryOperation.Multiply -> "key_multiply"
            BinaryOperation.Divide -> "key_divide"
        }
    }
