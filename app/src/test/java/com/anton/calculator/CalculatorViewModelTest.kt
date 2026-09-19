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

    @Test
    fun `addition shows the pending expression and completed result`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))

        assertEquals(
            CalculatorUiState(primaryValue = "0", secondaryExpression = "12 +"),
            viewModel.uiState.value,
        )

        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(
            CalculatorUiState(primaryValue = "15", secondaryExpression = "12 + 3 ="),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `subtraction can produce a negative result`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Subtract))
        viewModel.onAction(CalculatorAction.Digit(8))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(
            CalculatorUiState(primaryValue = "-5", secondaryExpression = "3 − 8 ="),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `multiplication produces the product`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(6))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.onAction(CalculatorAction.Digit(7))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("42", viewModel.uiState.value.primaryValue)
        assertEquals("6 × 7 =", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `division produces an ordinary finite quotient`() {
        val viewModel = CalculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(7))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("3.5", viewModel.uiState.value.primaryValue)
        assertEquals("7 ÷ 2 =", viewModel.uiState.value.secondaryExpression)
    }
}
