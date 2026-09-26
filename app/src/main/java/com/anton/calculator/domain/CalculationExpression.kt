package com.anton.calculator.domain

internal data class CalculationExpression(
    val leftOperandText: String,
    val operation: BinaryOperation,
    val rightOperandText: String,
)
