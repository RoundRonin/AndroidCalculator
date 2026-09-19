package com.anton.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalculatorUiState())
    private var pendingCalculation: PendingCalculation? = null

    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> appendDigit(action.value)
            is CalculatorAction.SelectOperation -> selectOperation(action.operation)
            CalculatorAction.Equals -> evaluate()
            CalculatorAction.ClearAll -> clearAll()
        }
    }

    private fun appendDigit(digit: Int) {
        val currentValue = mutableUiState.value.primaryValue
        val nextValue = if (currentValue == "0") digit.toString() else currentValue + digit
        mutableUiState.value = mutableUiState.value.copy(primaryValue = nextValue)
    }

    private fun selectOperation(operation: BinaryOperation) {
        val operandText = mutableUiState.value.primaryValue
        pendingCalculation = PendingCalculation(
            operand = operandText.toDouble(),
            operandText = operandText,
            operation = operation,
        )
        mutableUiState.value = CalculatorUiState(
            primaryValue = "0",
            secondaryExpression = "$operandText ${operation.symbol}",
        )
    }

    private fun evaluate() {
        val pending = pendingCalculation ?: return
        val rightText = mutableUiState.value.primaryValue
        val right = rightText.toDouble()
        val result = when (pending.operation) {
            BinaryOperation.Add -> pending.operand + right
            BinaryOperation.Subtract -> pending.operand - right
            BinaryOperation.Multiply -> pending.operand * right
            BinaryOperation.Divide -> pending.operand / right
        }
        mutableUiState.value = CalculatorUiState(
            primaryValue = result.toDisplayText(),
            secondaryExpression =
                "${pending.operandText} ${pending.operation.symbol} $rightText =",
        )
        pendingCalculation = null
    }

    private fun clearAll() {
        pendingCalculation = null
        mutableUiState.value = CalculatorUiState()
    }

    private fun Double.toDisplayText(): String =
        if (this % 1.0 == 0.0 && this in Long.MIN_VALUE.toDouble()..Long.MAX_VALUE.toDouble()) {
            toLong().toString()
        } else {
            toString()
        }

    private data class PendingCalculation(
        val operand: Double,
        val operandText: String,
        val operation: BinaryOperation,
    )
}
