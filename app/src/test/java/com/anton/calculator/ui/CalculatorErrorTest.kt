package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorErrorTest {
    @Test
    fun `unusual repeated input remains safe and recoverable`() {
        val viewModel = calculatorViewModel()
        val unusualSequence = listOf(
            CalculatorAction.Decimal,
            CalculatorAction.Decimal,
            CalculatorAction.Equals,
            CalculatorAction.SelectOperation(BinaryOperation.Divide),
            CalculatorAction.SelectOperation(BinaryOperation.Multiply),
            CalculatorAction.ClearEntry,
            CalculatorAction.Digit(0),
            CalculatorAction.Equals,
            CalculatorAction.SelectOperation(BinaryOperation.Add),
            CalculatorAction.Equals,
        )

        repeat(100) { unusualSequence.forEach(viewModel::onAction) }
        viewModel.onAction(CalculatorAction.ClearAll)
        viewModel.onAction(CalculatorAction.Digit(9))

        assertEquals(CalculatorUiState(primaryValue = "9"), viewModel.uiState.value)
    }

    @Test
    fun `division by zero produces an explicit error state`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(0))

        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("Error", viewModel.uiState.value.primaryValue)
        assertEquals(CalculatorDisplayStatus.Error, viewModel.uiState.value.displayStatus)
        assertEquals("1 ÷ 0 =", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `invalid intermediate calculation enters error without crashing`() {
        val viewModel = calculatorViewModel()
        viewModel.enter("1")
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.enter("0")

        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))

        assertEquals("Error", viewModel.uiState.value.primaryValue)
        assertEquals(CalculatorDisplayStatus.Error, viewModel.uiState.value.displayStatus)
    }

    @Test
    fun `zero divided by zero is an error rather than NaN`() {
        val viewModel = calculate("0", BinaryOperation.Divide, "0")

        assertEquals("Error", viewModel.uiState.value.primaryValue)
        assertEquals(CalculatorDisplayStatus.Error, viewModel.uiState.value.displayStatus)
    }

    @Test
    fun `overflow becomes an error rather than infinity`() {
        val viewModel = calculatorViewModel()
        viewModel.enter("999999999999999")

        repeat(24) {
            if (viewModel.uiState.value.displayStatus != CalculatorDisplayStatus.Error) {
                viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
                viewModel.enter("999999999999999")
                viewModel.onAction(CalculatorAction.Equals)
            }
        }

        assertEquals("Error", viewModel.uiState.value.primaryValue)
        assertEquals(CalculatorDisplayStatus.Error, viewModel.uiState.value.displayStatus)
    }

    @Test
    fun `digit after error starts a fresh calculation`() {
        val viewModel = errorViewModel()

        viewModel.onAction(CalculatorAction.Digit(7))

        assertEquals(CalculatorUiState(primaryValue = "7"), viewModel.uiState.value)
    }

    @Test
    fun `decimal after error starts a fresh decimal calculation`() {
        val viewModel = errorViewModel(decimalSeparator = ',')

        viewModel.onAction(CalculatorAction.Decimal)

        assertEquals(
            CalculatorUiState(primaryValue = "0,", decimalSeparator = ','),
            viewModel.uiState.value,
        )
    }

    @Test
    fun `both clear actions reset an error`() {
        val clearAll = errorViewModel()
        clearAll.onAction(CalculatorAction.ClearAll)
        assertEquals(CalculatorUiState(), clearAll.uiState.value)

        val clearEntry = errorViewModel()
        clearEntry.onAction(CalculatorAction.ClearEntry)
        assertEquals(CalculatorUiState(), clearEntry.uiState.value)
    }

    @Test
    fun `operation and equals leave an error unchanged`() {
        val viewModel = errorViewModel()
        val error = viewModel.uiState.value

        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals(error, viewModel.uiState.value)
    }
}

