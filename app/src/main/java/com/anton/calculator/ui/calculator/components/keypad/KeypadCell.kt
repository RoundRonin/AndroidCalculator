package com.anton.calculator.ui.calculator.components.keypad

import com.anton.calculator.domain.calculation.CalculatorAction

internal data class KeypadCell(
    val action: CalculatorAction? = null,
    val weight: Float = 1f,
)
