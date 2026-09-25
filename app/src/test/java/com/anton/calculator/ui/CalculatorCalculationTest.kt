package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorCalculationTest {
    @Test
    fun `calculator starts at zero with no expression`() {
        val viewModel = calculatorViewModel()

        assertEquals(
            CalculatorUiState(primaryValue = "0", secondaryExpression = ""),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `digit actions build a whole number`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))

        assertEquals("12", viewModel.uiState.value.primaryValue)
        assertEquals("", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `redundant leading zeroes stay normalized`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(0))
        viewModel.onAction(CalculatorAction.Digit(0))
        viewModel.onAction(CalculatorAction.Digit(7))

        assertEquals("7", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `clear all restores the initial display`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.Digit(2))

        viewModel.onAction(CalculatorAction.ClearAll)

        assertEquals(CalculatorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `addition shows the pending expression and completed result`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))

        assertEquals(
            CalculatorUiState(
                primaryValue = "0",
                secondaryExpression = "12 +",
                selectedOperation = BinaryOperation.Add,
            ),
            viewModel.uiState.value,
        )

        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(
            CalculatorUiState(
                primaryValue = "15",
                secondaryExpression = "12 + 3 =",
                displayStatus = CalculatorDisplayStatus.Result,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `subtraction can produce a negative result`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Subtract))
        viewModel.onAction(CalculatorAction.Digit(8))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(
            CalculatorUiState(
                primaryValue = "-5",
                secondaryExpression = "3 − 8 =",
                displayStatus = CalculatorDisplayStatus.Result,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `multiplication produces the product`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(6))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.onAction(CalculatorAction.Digit(7))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("42", viewModel.uiState.value.primaryValue)
        assertEquals("6 × 7 =", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `division produces an ordinary finite quotient`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(7))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("3.5", viewModel.uiState.value.primaryValue)
        assertEquals("7 ÷ 2 =", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `selecting another operation before a second operand replaces it`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))

        assertEquals(
            CalculatorUiState(
                primaryValue = "0",
                secondaryExpression = "12 ×",
                selectedOperation = BinaryOperation.Multiply,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `selecting an operation after a second operand chains from the intermediate result`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))

        assertEquals(
            CalculatorUiState(
                primaryValue = "0",
                secondaryExpression = "15 ×",
                selectedOperation = BinaryOperation.Multiply,
            ),
            viewModel.uiState.value,
        )

        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(
            CalculatorUiState(
                primaryValue = "30",
                secondaryExpression = "15 × 2 =",
                displayStatus = CalculatorDisplayStatus.Result,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `equals leaves an incomplete calculation unchanged`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(9))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Subtract))
        val pendingState = viewModel.uiState.value

        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(pendingState, viewModel.uiState.value)
    }

    @Test
    fun `digit after a result starts a new calculation`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)

        viewModel.onAction(CalculatorAction.Digit(7))

        assertEquals(CalculatorUiState(primaryValue = "7"), viewModel.uiState.value)
    }

    @Test
    fun `clear entry clears the second operand but preserves the pending calculation`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(3))

        viewModel.onAction(CalculatorAction.ClearEntry)

        assertEquals(
            CalculatorUiState(
                primaryValue = "0",
                secondaryExpression = "12 +",
                selectedOperation = BinaryOperation.Add,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `clear entry before second operand input leaves the calculation unchanged`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(8))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        val pendingState = viewModel.uiState.value

        viewModel.onAction(CalculatorAction.ClearEntry)

        assertEquals(pendingState, viewModel.uiState.value)
    }

    @Test
    fun `equals after clear entry leaves the pending calculation unchanged`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(8))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.ClearEntry)
        val clearedState = viewModel.uiState.value

        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(clearedState, viewModel.uiState.value)
    }

    @Test
    fun `clear entry after a result restores the initial state`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.onAction(CalculatorAction.Digit(5))
        viewModel.onAction(CalculatorAction.Equals)

        viewModel.onAction(CalculatorAction.ClearEntry)

        assertEquals(CalculatorUiState(), viewModel.uiState.value)
    }

    @Test
    fun `operator after a result continues from that result`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Equals)

        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))

        assertEquals(
            CalculatorUiState(
                primaryValue = "0",
                secondaryExpression = "3 ×",
                selectedOperation = BinaryOperation.Multiply,
            ),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `repeated equals leaves a completed result unchanged`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(5))
        viewModel.onAction(CalculatorAction.Equals)
        val completedState = viewModel.uiState.value

        viewModel.onAction(CalculatorAction.Equals)
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(completedState, viewModel.uiState.value)
    }

    @Test
    fun `clear all resets partial chained and completed calculations`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(9))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Subtract))
        viewModel.onAction(CalculatorAction.ClearAll)
        assertEquals(CalculatorUiState(), viewModel.uiState.value)

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.onAction(CalculatorAction.ClearAll)
        assertEquals(CalculatorUiState(), viewModel.uiState.value)

        viewModel.onAction(CalculatorAction.Digit(6))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.Equals)
        viewModel.onAction(CalculatorAction.ClearAll)
        assertEquals(CalculatorUiState(), viewModel.uiState.value)
    }
}

