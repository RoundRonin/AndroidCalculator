package com.anton.calculator.domain.calculation.state

import com.anton.calculator.domain.calculation.BinaryOperation

internal data class CalculationExpression(
    val leftOperandText: String,
    val operation: BinaryOperation,
    val rightOperandText: String,
)
