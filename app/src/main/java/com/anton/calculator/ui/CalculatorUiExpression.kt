package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation

internal data class CalculatorUiExpression(
    val leftOperand: String,
    val operation: BinaryOperation,
    val rightOperand: String? = null,
)
