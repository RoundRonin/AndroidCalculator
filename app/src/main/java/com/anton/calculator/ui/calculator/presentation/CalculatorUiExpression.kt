package com.anton.calculator.ui.calculator.presentation

import com.anton.calculator.domain.calculation.BinaryOperation

internal data class CalculatorUiExpression(
    val leftOperand: String,
    val operation: BinaryOperation,
    val rightOperand: String? = null,
)
