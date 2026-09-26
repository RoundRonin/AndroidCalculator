package com.anton.calculator.domain.calculation.state

import com.anton.calculator.domain.calculation.BinaryOperation

internal data class PendingCalculation(
    val operand: Double,
    val operandText: String,
    val operation: BinaryOperation,
)
