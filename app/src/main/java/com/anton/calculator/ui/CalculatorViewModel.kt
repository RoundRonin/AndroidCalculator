package com.anton.calculator.ui

import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.domain.CalculatorReducer
import com.anton.calculator.persistence.CalculatorStateStore

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class CalculatorViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val stateStore = CalculatorStateStore(savedStateHandle)
    private var calculatorState = stateStore.restore()
    private var decimalSeparator = DEFAULT_DECIMAL_SEPARATOR
    private val mutableUiState = MutableStateFlow(calculatorState.toUiState(decimalSeparator))

    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        calculatorState = CalculatorReducer.reduce(calculatorState, action)
        stateStore.save(calculatorState)
        publishUiState()
    }

    fun onDecimalSeparatorChanged(separator: Char) {
        if (separator == decimalSeparator) return
        decimalSeparator = separator
        publishUiState()
    }

    private fun publishUiState() {
        mutableUiState.value = calculatorState.toUiState(decimalSeparator)
    }

    private companion object {
        const val DEFAULT_DECIMAL_SEPARATOR = '.'
    }
}
