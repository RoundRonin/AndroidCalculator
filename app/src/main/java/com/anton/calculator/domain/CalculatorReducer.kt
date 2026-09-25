package com.anton.calculator.domain

internal object CalculatorReducer {
    fun reduce(state: CalculatorState, action: CalculatorAction): CalculatorState =
        when (action) {
            is CalculatorAction.Digit -> appendDigit(state, action.value)
            CalculatorAction.Decimal -> appendDecimal(state)
            is CalculatorAction.SelectOperation -> selectOperation(state, action.operation)
            CalculatorAction.Equals -> evaluate(state)
            CalculatorAction.ClearEntry -> clearEntry(state)
            CalculatorAction.ClearAll -> CalculatorState.initial()
        }

    private fun appendDigit(state: CalculatorState, digit: Int): CalculatorState {
        val editableState = state.prepareForInput()
        return when (editableState) {
            is CalculatorState.EnteringFirstOperand -> editableState.entry
                .appendCalculatorDigit(digit)?.let { editableState.copy(entry = it) }
                ?: editableState
            is CalculatorState.EnteringSecondOperand -> editableState.entry
                .appendCalculatorDigit(digit)?.let { editableState.copy(entry = it) }
                ?: editableState
            else -> editableState
        }
    }

    private fun appendDecimal(state: CalculatorState): CalculatorState {
        val editableState = state.prepareForInput()
        return when (editableState) {
            is CalculatorState.EnteringFirstOperand -> editableState.copy(
                entry = editableState.entry.withDecimal(),
            )
            is CalculatorState.EnteringSecondOperand -> editableState.copy(
                entry = editableState.entry.withDecimal(),
            )
            else -> editableState
        }
    }

    private fun selectOperation(
        state: CalculatorState,
        operation: BinaryOperation,
    ): CalculatorState = when (state) {
        is CalculatorState.Error -> state
        is CalculatorState.OperationPending -> state.copy(
            calculation = state.calculation.copy(operation = operation),
        )
        is CalculatorState.EnteringSecondOperand -> when (val result = evaluate(state)) {
            is CalculatorState.Result -> result.startOperation(operation)
            is CalculatorState.Error -> result
            else -> result
        }
        is CalculatorState.EnteringFirstOperand -> CalculatorState.OperationPending(
            PendingCalculation(
                operand = state.entry.toDouble(),
                operandText = state.entry,
                operation = operation,
            ),
        )
        is CalculatorState.Result -> state.startOperation(operation)
    }

    private fun evaluate(state: CalculatorState): CalculatorState {
        if (state !is CalculatorState.EnteringSecondOperand) return state
        val expression = CalculationExpression(
            leftOperandText = state.calculation.operandText,
            operation = state.calculation.operation,
            rightOperandText = state.entry,
        )
        val result = state.calculation.operation.apply(
            left = state.calculation.operand,
            right = state.entry.toDouble(),
        )
        return if (result.isFinite()) {
            CalculatorState.Result(value = result, expression = expression)
        } else {
            CalculatorState.Error(expression)
        }
    }

    private fun clearEntry(state: CalculatorState): CalculatorState = when (state) {
        is CalculatorState.EnteringSecondOperand ->
            CalculatorState.OperationPending(state.calculation)
        is CalculatorState.OperationPending -> state
        is CalculatorState.EnteringFirstOperand,
        is CalculatorState.Result,
        is CalculatorState.Error,
        -> CalculatorState.initial()
    }

    private fun CalculatorState.prepareForInput(): CalculatorState = when (this) {
        is CalculatorState.Result,
        is CalculatorState.Error,
        -> CalculatorState.initial()
        is CalculatorState.OperationPending ->
            CalculatorState.EnteringSecondOperand(calculation)
        else -> this
    }

    private fun CalculatorState.Result.startOperation(
        operation: BinaryOperation,
    ): CalculatorState.OperationPending = CalculatorState.OperationPending(
        PendingCalculation(
            operand = value,
            operandText = value.toCalculatorText(),
            operation = operation,
        ),
    )

    private fun String.withDecimal(): String = if ('.' in this) this else "$this."
}
