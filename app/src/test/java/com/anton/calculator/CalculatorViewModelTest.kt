package com.anton.calculator

import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorViewModelTest {
    @Test
    fun `calculator starts at zero with no expression`() {
        val viewModel = CalculatorViewModel()

        assertEquals(
            CalculatorUiState(primaryValue = "0", secondaryExpression = ""),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `digit actions build a whole number`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))

        assertEquals("12", viewModel.uiState.value.primaryValue)
        assertEquals("", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `redundant leading zeroes stay normalized`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(0))
        viewModel.onAction(CalculatorAction.Digit(0))
        viewModel.onAction(CalculatorAction.Digit(7))

        assertEquals("7", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `clear all restores the initial display`() {
        val viewModel = CalculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.Digit(2))

        viewModel.onAction(CalculatorAction.ClearAll)

        assertEquals(CalculatorUiState(), viewModel.uiState.value)
    }
}
