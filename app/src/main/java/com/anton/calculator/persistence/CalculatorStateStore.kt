package com.anton.calculator.persistence

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculationExpression
import com.anton.calculator.domain.CalculatorState
import com.anton.calculator.domain.PendingCalculation

import android.os.Bundle
import androidx.lifecycle.SavedStateHandle

internal class ddiCalculatorStateStore(
    private val savedStateHandle: SavedStateHandle,
) {
    fun restore(): CalculatorState = savedStateHandle.get<Bundle>(STATE_KEY)
        ?.toCalculatorState()
        ?: CalculatorState.initial()

    fun save(state: CalculatorState) {
        savedStateHandle[STATE_KEY] = state.toBundle()
    }

    private fun CalculatorState.toBundle(): Bundle = when (this) {
        is CalculatorState.EnteringFirstOperand -> Bundle().apply {
            putString(TYPE_KEY, TYPE_FIRST_OPERAND)
            putString(ENTRY_KEY, entry)
        }
        is CalculatorState.OperationPending -> calculation.toBundle(TYPE_OPERATION_PENDING)
        is CalculatorState.EnteringSecondOperand -> calculation
            .toBundle(TYPE_SECOND_OPERAND)
            .apply { putString(ENTRY_KEY, entry) }
        is CalculatorState.Result -> expression
            .toBundle(TYPE_RESULT)
            .apply { putDouble(RESULT_VALUE_KEY, value) }
        is CalculatorState.Error -> expression.toBundle(TYPE_ERROR)
    }

    private fun PendingCalculation.toBundle(type: String): Bundle = Bundle().apply {
        putString(TYPE_KEY, type)
        putDouble(LEFT_VALUE_KEY, operand)
        putString(LEFT_TEXT_KEY, operandText)
        putString(OPERATION_KEY, operation.name)
    }

    private fun CalculationExpression.toBundle(type: String): Bundle = Bundle().apply {
        putString(TYPE_KEY, type)
        putString(LEFT_TEXT_KEY, leftOperandText)
        putString(OPERATION_KEY, operation.name)
        putString(RIGHT_TEXT_KEY, rightOperandText)
    }

    private fun Bundle.toCalculatorState(): CalculatorState? = when (getString(TYPE_KEY)) {
        TYPE_FIRST_OPERAND -> getString(ENTRY_KEY)
            ?.takeIf { entry -> entry.isValidEntry() }
            ?.let(CalculatorState::EnteringFirstOperand)
        TYPE_OPERATION_PENDING -> toPendingCalculation()
            ?.let(CalculatorState::OperationPending)
        TYPE_SECOND_OPERAND -> {
            val calculation = toPendingCalculation()
            val entry = getString(ENTRY_KEY)?.takeIf { value -> value.isValidEntry() }
            if (calculation != null && entry != null) {
                CalculatorState.EnteringSecondOperand(calculation, entry)
            } else {
                null
            }
        }
        TYPE_RESULT -> {
            val expression = toExpression()
            val value = getDouble(RESULT_VALUE_KEY, Double.NaN)
            if (expression != null && value.isFinite()) {
                CalculatorState.Result(value, expression)
            } else {
                null
            }
        }
        TYPE_ERROR -> toExpression()?.let(CalculatorState::Error)
        else -> null
    }

    private fun Bundle.toPendingCalculation(): PendingCalculation? {
        val operand = getDouble(LEFT_VALUE_KEY, Double.NaN).takeIf(Double::isFinite)
        val operandText = getString(LEFT_TEXT_KEY)?.takeIf { value -> value.isValidEntry() }
        val operation = getString(OPERATION_KEY)?.toOperation()
        return if (operand != null && operandText != null && operation != null) {
            PendingCalculation(operand, operandText, operation)
        } else {
            null
        }
    }

    private fun Bundle.toExpression(): CalculationExpression? {
        val leftText = getString(LEFT_TEXT_KEY)?.takeIf { value -> value.isValidEntry() }
        val rightText = getString(RIGHT_TEXT_KEY)?.takeIf { value -> value.isValidEntry() }
        val operation = getString(OPERATION_KEY)?.toOperation()
        return if (leftText != null && rightText != null && operation != null) {
            CalculationExpression(leftText, operation, rightText)
        } else {
            null
        }
    }

    private fun String.toOperation(): BinaryOperation? =
        BinaryOperation.entries.find { operation -> operation.name == this }

    private fun String.isValidEntry(): Boolean =
        isNotEmpty() && length <= MAX_RESTORED_ENTRY_LENGTH && toDoubleOrNull()?.isFinite() == true

    private companion object {
        const val STATE_KEY = "calculatorState"
        const val TYPE_KEY = "type"
        const val ENTRY_KEY = "entry"
        const val LEFT_VALUE_KEY = "leftValue"
        const val LEFT_TEXT_KEY = "leftText"
        const val RIGHT_TEXT_KEY = "rightText"
        const val OPERATION_KEY = "operation"
        const val RESULT_VALUE_KEY = "resultValue"
        const val TYPE_FIRST_OPERAND = "firstOperand"
        const val TYPE_OPERATION_PENDING = "operationPending"
        const val TYPE_SECOND_OPERAND = "secondOperand"
        const val TYPE_RESULT = "result"
        const val TYPE_ERROR = "error"
        const val MAX_RESTORED_ENTRY_LENGTH = 32
    }
}
