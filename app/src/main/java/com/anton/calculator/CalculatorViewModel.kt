package com.anton.calculator

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CalculatorViewModel : ViewModel() {
    private val mutableUiState = MutableStateFlow(CalculatorUiState())

    val uiState: StateFlow<CalculatorUiState> = mutableUiState.asStateFlow()

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Digit -> appendDigit(action.value)
            CalculatorAction.ClearAll -> mutableUiState.value = CalculatorUiState()
        }
    }

    private fun appendDigit(digit: Int) {
        val currentValue = mutableUiState.value.primaryValue
        val nextValue = if (currentValue == "0") digit.toString() else currentValue + digit
        mutableUiState.value = mutableUiState.value.copy(primaryValue = nextValue)
    }
}
