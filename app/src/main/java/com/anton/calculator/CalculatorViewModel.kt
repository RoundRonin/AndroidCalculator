package com.anton.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.text.DecimalFormatSymbols

class CalculatorViewModel(
    private val savedStateHandle: SavedStateHandle,
    configuredDecimalSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator,
) : ViewModel() {
    constructor(savedStateHandle: SavedStateHandle) : this(
        savedStateHandle = savedStateHandle,
        configuredDecimalSeparator = DecimalFormatSymbols.getInstance().decimalSeparator,
    )

    constructor(decimalSeparator: Char = DecimalFormatSymbols.getInstance().decimalSeparator) : this(
        savedStateHandle = SavedStateHandle(),
        configuredDecimalSeparator = decimalSeparator,
    )

    private val decimalSeparator = savedStateHandle.get<String>(DECIMAL_SEPARATOR_KEY)
        ?.singleOrNull()
        ?: configuredDecimalSeparator
    private val restoredState = restoreState()
    private var phase: CalculationPhase = restoredState.phase
    private val mutableUiState = MutableStateFlow(restoredState.uiState)

    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> appendDigit(action.value)
            CalculatorAction.Decimal -> appendDecimal()
            is CalculatorAction.SelectOperation -> selectOperation(action.operation)
            CalculatorAction.Equals -> evaluate()
            CalculatorAction.ClearEntry -> clearEntry()
            CalculatorAction.ClearAll -> clearAll()
        }
        saveState()
    }

    private fun appendDecimal() {
        prepareForInput()
        val currentValue = mutableUiState.value.primaryValue
        if (decimalSeparator !in currentValue) {
            mutableUiState.value = mutableUiState.value.copy(
                primaryValue = if (currentValue == "0") {
                    "0$decimalSeparator"
                } else {
                    currentValue + decimalSeparator
                },
            )
        }
    }

    private fun appendDigit(digit: Int) {
        prepareForInput()
        val currentValue = mutableUiState.value.primaryValue
        val nextValue = if (currentValue == "0") digit.toString() else currentValue + digit
        if (nextValue.significantDigitCount() > MAX_INPUT_DIGITS) return
        mutableUiState.value = mutableUiState.value.copy(primaryValue = nextValue)
    }

    private fun selectOperation(operation: BinaryOperation) {
        when (val currentPhase = phase) {
            CalculationPhase.Error -> return
            is CalculationPhase.OperationPending -> {
                val replacement = currentPhase.calculation.copy(operation = operation)
                phase = CalculationPhase.OperationPending(replacement)
                mutableUiState.value = mutableUiState.value.copy(
                    secondaryExpression = "${replacement.operandText} ${operation.symbol}",
                )
                return
            }
            is CalculationPhase.EnteringSecondOperand -> {
                evaluate()
                if (phase == CalculationPhase.Error) return
            }
            else -> Unit
        }
        val operandText = mutableUiState.value.primaryValue
        val operand = (phase as? CalculationPhase.Result)?.value ?: operandText.toInternalDouble()
        phase = CalculationPhase.OperationPending(
            PendingCalculation(
                operand = operand,
                operandText = operandText,
                operation = operation,
            ),
        )
        mutableUiState.value = initialUiState().copy(
            primaryValue = "0",
            secondaryExpression = "$operandText ${operation.symbol}",
        )
    }

    private fun evaluate() {
        val pending = (phase as? CalculationPhase.EnteringSecondOperand)?.calculation ?: return
        val rightText = mutableUiState.value.primaryValue
        val right = rightText.toInternalDouble()
        val result = when (pending.operation) {
            BinaryOperation.Add -> pending.operand + right
            BinaryOperation.Subtract -> pending.operand - right
            BinaryOperation.Multiply -> pending.operand * right
            BinaryOperation.Divide -> pending.operand / right
        }
        val expression = "${pending.operandText} ${pending.operation.symbol} $rightText ="
        if (!result.isFinite()) {
            mutableUiState.value = initialUiState().copy(
                primaryValue = "Error",
                secondaryExpression = expression,
                displayStatus = CalculatorDisplayStatus.Error,
            )
            phase = CalculationPhase.Error
            return
        }
        mutableUiState.value = initialUiState().copy(
            primaryValue = result.toDisplayText(),
            secondaryExpression = expression,
            displayStatus = CalculatorDisplayStatus.Result,
        )
        phase = CalculationPhase.Result(result)
    }

    private fun clearAll() {
        phase = CalculationPhase.EnteringFirstOperand
        mutableUiState.value = initialUiState()
    }

    private fun initialUiState() = CalculatorUiState(decimalSeparator = decimalSeparator)

    private fun restoreState(): RestoredCalculatorState {
        val initialState = RestoredCalculatorState(
            uiState = initialUiState(),
            phase = CalculationPhase.EnteringFirstOperand,
        )
        val primaryValue = savedStateHandle.get<String>(PRIMARY_VALUE_KEY) ?: return initialState
        val phaseName = savedStateHandle.get<String>(PHASE_KEY)
            ?: return initialState
        val pendingCalculation = restorePendingCalculation()
        val restoredPhase = when (phaseName) {
            PHASE_FIRST_OPERAND -> CalculationPhase.EnteringFirstOperand
            PHASE_OPERATION_PENDING -> pendingCalculation
                ?.let(CalculationPhase::OperationPending)
                ?: return initialState
            PHASE_SECOND_OPERAND -> pendingCalculation
                ?.let(CalculationPhase::EnteringSecondOperand)
                ?: return initialState
            PHASE_RESULT -> savedStateHandle.get<Double>(RESULT_VALUE_KEY)
                ?.let(CalculationPhase::Result)
                ?: return initialState
            PHASE_ERROR -> CalculationPhase.Error
            else -> return initialState
        }
        val displayStatus = when (restoredPhase) {
            is CalculationPhase.Result -> CalculatorDisplayStatus.Result
            CalculationPhase.Error -> CalculatorDisplayStatus.Error
            else -> CalculatorDisplayStatus.Editing
        }
        return RestoredCalculatorState(
            uiState = CalculatorUiState(
                primaryValue = primaryValue,
                secondaryExpression = savedStateHandle[SECONDARY_EXPRESSION_KEY] ?: "",
                decimalSeparator = decimalSeparator,
                displayStatus = displayStatus,
            ),
            phase = restoredPhase,
        )
    }

    private fun restorePendingCalculation(): PendingCalculation? {
        val operand = savedStateHandle.get<Double>(PENDING_OPERAND_KEY) ?: return null
        val operandText = savedStateHandle.get<String>(PENDING_OPERAND_TEXT_KEY) ?: return null
        val operationName = savedStateHandle.get<String>(PENDING_OPERATION_KEY) ?: return null
        val operation = BinaryOperation.entries.find { it.name == operationName } ?: return null
        return PendingCalculation(operand, operandText, operation)
    }

    private fun saveState() {
        val state = mutableUiState.value
        savedStateHandle[PRIMARY_VALUE_KEY] = state.primaryValue
        savedStateHandle[SECONDARY_EXPRESSION_KEY] = state.secondaryExpression
        savedStateHandle[DECIMAL_SEPARATOR_KEY] = decimalSeparator.toString()
        savedStateHandle[PHASE_KEY] = phase.savedName

        val pendingCalculation = when (val currentPhase = phase) {
            is CalculationPhase.OperationPending -> currentPhase.calculation
            is CalculationPhase.EnteringSecondOperand -> currentPhase.calculation
            else -> null
        }
        savedStateHandle[PENDING_OPERAND_KEY] = pendingCalculation?.operand
        savedStateHandle[PENDING_OPERAND_TEXT_KEY] = pendingCalculation?.operandText
        savedStateHandle[PENDING_OPERATION_KEY] = pendingCalculation?.operation?.name
        savedStateHandle[RESULT_VALUE_KEY] = (phase as? CalculationPhase.Result)?.value
    }

    private fun prepareForInput() {
        phase = when (val currentPhase = phase) {
            is CalculationPhase.Result,
            CalculationPhase.Error,
            -> {
                mutableUiState.value = initialUiState()
                CalculationPhase.EnteringFirstOperand
            }
            is CalculationPhase.OperationPending ->
                CalculationPhase.EnteringSecondOperand(currentPhase.calculation)
            else -> currentPhase
        }
    }

    private fun String.toInternalDouble(): Double =
        replace(decimalSeparator, '.').toDouble()

    private fun String.significantDigitCount(): Int =
        filter(Char::isDigit).dropWhile { digit -> digit == '0' }.length

    private fun clearEntry() {
        when (val currentPhase = phase) {
            is CalculationPhase.EnteringSecondOperand -> {
                phase = CalculationPhase.OperationPending(currentPhase.calculation)
                mutableUiState.value = mutableUiState.value.copy(primaryValue = "0")
            }
            is CalculationPhase.OperationPending -> Unit
            CalculationPhase.EnteringFirstOperand,
            is CalculationPhase.Result,
            CalculationPhase.Error,
            -> clearAll()
        }
    }

    private fun Double.toDisplayText(): String {
        if (this == 0.0) return "0"
        val rounded = BigDecimal.valueOf(this)
            .round(MathContext(RESULT_SIGNIFICANT_DIGITS, RoundingMode.HALF_UP))
            .stripTrailingZeros()
        val plainText = rounded.toPlainString()
        val text = plainText.takeIf { it.length <= MAX_PLAIN_RESULT_CHARACTERS }
            ?: rounded.toString()
        return text
            .replace('.', decimalSeparator)
    }

    private data class PendingCalculation(
        val operand: Double,
        val operandText: String,
        val operation: BinaryOperation,
    )

    private data class RestoredCalculatorState(
        val uiState: CalculatorUiState,
        val phase: CalculationPhase,
    )

    private sealed interface CalculationPhase {
        data object EnteringFirstOperand : CalculationPhase
        data class OperationPending(val calculation: PendingCalculation) : CalculationPhase
        data class EnteringSecondOperand(val calculation: PendingCalculation) : CalculationPhase
        data class Result(val value: Double) : CalculationPhase
        data object Error : CalculationPhase
    }

    private val CalculationPhase.savedName: String
        get() = when (this) {
            CalculationPhase.EnteringFirstOperand -> PHASE_FIRST_OPERAND
            is CalculationPhase.OperationPending -> PHASE_OPERATION_PENDING
            is CalculationPhase.EnteringSecondOperand -> PHASE_SECOND_OPERAND
            is CalculationPhase.Result -> PHASE_RESULT
            CalculationPhase.Error -> PHASE_ERROR
        }

    private companion object {
        const val MAX_INPUT_DIGITS = 15
        const val RESULT_SIGNIFICANT_DIGITS = 12
        const val MAX_PLAIN_RESULT_CHARACTERS = 16
        const val PRIMARY_VALUE_KEY = "primaryValue"
        const val SECONDARY_EXPRESSION_KEY = "secondaryExpression"
        const val DECIMAL_SEPARATOR_KEY = "decimalSeparator"
        const val PHASE_KEY = "phase"
        const val PENDING_OPERAND_KEY = "pendingOperand"
        const val PENDING_OPERAND_TEXT_KEY = "pendingOperandText"
        const val PENDING_OPERATION_KEY = "pendingOperation"
        const val RESULT_VALUE_KEY = "resultValue"
        const val PHASE_FIRST_OPERAND = "firstOperand"
        const val PHASE_OPERATION_PENDING = "operationPending"
        const val PHASE_SECOND_OPERAND = "secondOperand"
        const val PHASE_RESULT = "result"
        const val PHASE_ERROR = "error"
    }
}
