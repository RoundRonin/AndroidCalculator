package com.anton.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalculatorUiState())
    private var phase: CalculationPhase = CalculationPhase.EnteringFirstOperand

    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> appendDigit(action.value)
            is CalculatorAction.SelectOperation -> selectOperation(action.operation)
            CalculatorAction.Equals -> evaluate()
            CalculatorAction.ClearEntry -> clearEntry()
            CalculatorAction.ClearAll -> clearAll()
        }
    }

    private fun appendDigit(digit: Int) {
        phase = when (val currentPhase = phase) {
            CalculationPhase.Result -> {
                mutableUiState.value = CalculatorUiState()
                CalculationPhase.EnteringFirstOperand
            }
            is CalculationPhase.OperationPending ->
                CalculationPhase.EnteringSecondOperand(currentPhase.calculation)
            else -> currentPhase
        }
        val currentValue = mutableUiState.value.primaryValue
        val nextValue = if (currentValue == "0") digit.toString() else currentValue + digit
        mutableUiState.value = mutableUiState.value.copy(primaryValue = nextValue)
    }

    private fun selectOperation(operation: BinaryOperation) {
        when (val currentPhase = phase) {
            is CalculationPhase.OperationPending -> {
                val replacement = currentPhase.calculation.copy(operation = operation)
                phase = CalculationPhase.OperationPending(replacement)
                mutableUiState.value = mutableUiState.value.copy(
                    secondaryExpression = "${replacement.operandText} ${operation.symbol}",
                )
                return
            }
            is CalculationPhase.EnteringSecondOperand -> evaluate()
            else -> Unit
        }
        val operandText = mutableUiState.value.primaryValue
        phase = CalculationPhase.OperationPending(
            PendingCalculation(
                operand = operandText.toDouble(),
                operandText = operandText,
                operation = operation,
            ),
        )
        mutableUiState.value = CalculatorUiState(
            primaryValue = "0",
            secondaryExpression = "$operandText ${operation.symbol}",
        )
    }

    private fun evaluate() {
        val pending = (phase as? CalculationPhase.EnteringSecondOperand)?.calculation ?: return
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
        phase = CalculationPhase.Result
    }

    private fun clearAll() {
        phase = CalculationPhase.EnteringFirstOperand
        mutableUiState.value = CalculatorUiState()
    }

    private fun clearEntry() {
        when (val currentPhase = phase) {
            is CalculationPhase.EnteringSecondOperand -> {
                phase = CalculationPhase.OperationPending(currentPhase.calculation)
                mutableUiState.value = mutableUiState.value.copy(primaryValue = "0")
            }
            is CalculationPhase.OperationPending -> Unit
            CalculationPhase.EnteringFirstOperand,
            CalculationPhase.Result,
            -> clearAll()
        }
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

    private sealed interface CalculationPhase {
        data object EnteringFirstOperand : CalculationPhase
        data class OperationPending(val calculation: PendingCalculation) : CalculationPhase
        data class EnteringSecondOperand(val calculation: PendingCalculation) : CalculationPhase
        data object Result : CalculationPhase
    }
}
