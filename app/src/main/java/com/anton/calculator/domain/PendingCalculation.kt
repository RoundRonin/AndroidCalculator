package com.anton.calculator.domain

internal data class PendingCalculation(
    val operand: Double,
    val operandText: String,
    val operation: BinaryOperation,
)
