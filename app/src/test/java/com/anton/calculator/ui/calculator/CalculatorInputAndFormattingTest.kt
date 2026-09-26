package com.anton.calculator.ui.calculator

import com.anton.calculator.domain.calculation.BinaryOperation
import com.anton.calculator.domain.calculation.CalculatorAction
import com.anton.calculator.ui.calculator.presentation.CalculatorUiExpression

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorInputAndFormattingTest {
    @Test
    fun `decimal starts an operand with the configured separator`() {
        val viewModel = calculatorViewModel(',')

        viewModel.onAction(CalculatorAction.Decimal)

        assertEquals("0,", viewModel.uiState.value.primaryValue)
        assertEquals(',', viewModel.uiState.value.decimalSeparator)
    }

    @Test
    fun `an operand accepts only one decimal separator`() {
        val viewModel = calculatorViewModel()

        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Digit(2))
        viewModel.onAction(CalculatorAction.Decimal)

        assertEquals("1.2", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `decimal starts the second operand without changing the pending operation`() {
        val viewModel = calculatorViewModel(',')
        viewModel.onAction(CalculatorAction.Digit(4))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))

        viewModel.onAction(CalculatorAction.Decimal)

        assertEquals("0,", viewModel.uiState.value.primaryValue)
        assertEquals(
            CalculatorUiExpression("4", BinaryOperation.Add),
            viewModel.uiState.value.expression,
        )
        viewModel.onAction(CalculatorAction.Digit(5))
        viewModel.onAction(CalculatorAction.Equals)
        assertEquals("4,5", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `manual input stops after fifteen significant digits`() {
        val viewModel = calculatorViewModel()

        "1234567890123456".forEach { character ->
            viewModel.onAction(CalculatorAction.Digit(character.digitToInt()))
        }

        assertEquals("123456789012345", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `synthesized zero does not consume the decimal input limit`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Decimal)

        "1234567890123456".forEach { character ->
            viewModel.onAction(CalculatorAction.Digit(character.digitToInt()))
        }

        assertEquals("0.123456789012345", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `fractional leading zeroes do not consume the significant digit limit`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Decimal)
        repeat(10) { viewModel.onAction(CalculatorAction.Digit(0)) }

        "1234567890123456".forEach { character ->
            viewModel.onAction(CalculatorAction.Digit(character.digitToInt()))
        }

        assertEquals("0.0000000000123456789012345", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `fractional leading zeroes cannot grow the entry without bound`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Decimal)

        repeat(100) { viewModel.onAction(CalculatorAction.Digit(0)) }

        assertEquals(32, requireNotNull(viewModel.uiState.value.primaryValue).length)
    }

    @Test
    fun `completed results hide familiar floating point noise`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.onAction(CalculatorAction.Decimal)
        viewModel.onAction(CalculatorAction.Digit(2))

        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("0.3", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `chaining preserves the exact result behind rounded display text`() {
        val viewModel = calculatorViewModel()
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
        viewModel.onAction(CalculatorAction.Digit(3))
        viewModel.onAction(CalculatorAction.Equals)
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.onAction(CalculatorAction.Digit(1))
        viewModel.onAction(CalculatorAction.Digit(1))

        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("3.66666666667", viewModel.uiState.value.primaryValue)
    }

    @Test
    fun `large results use scientific notation only beyond the plain display threshold`() {
        val plain = calculatorViewModel()
        "100000000000".forEach { character ->
            plain.onAction(CalculatorAction.Digit(character.digitToInt()))
        }
        plain.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        plain.onAction(CalculatorAction.Digit(0))
        plain.onAction(CalculatorAction.Equals)
        assertEquals("100000000000", plain.uiState.value.primaryValue)

        val scientific = calculatorViewModel()
        "1000000000000".forEach { character ->
            scientific.onAction(CalculatorAction.Digit(character.digitToInt()))
        }
        scientific.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        scientific.onAction(CalculatorAction.Digit(0))
        scientific.onAction(CalculatorAction.Equals)
        assertEquals("1000000000000", scientific.uiState.value.primaryValue)

        assertEquals(
            "1E+28",
            calculate(
                "100000000000000",
                BinaryOperation.Multiply,
                "100000000000000",
            ).uiState.value.primaryValue,
        )
    }

    @Test
    fun `result formatting is concise and uses stable scientific thresholds`() {
        assertEquals("2.5", calculate("5", BinaryOperation.Divide, "2").uiState.value.primaryValue)
        assertEquals(
            "0.666666666667",
            calculate("2", BinaryOperation.Divide, "3").uiState.value.primaryValue,
        )
        assertEquals(
            "0.000001",
            calculate("1", BinaryOperation.Divide, "1000000").uiState.value.primaryValue,
        )
        assertEquals(
            "0.0000001",
            calculate("1", BinaryOperation.Divide, "10000000").uiState.value.primaryValue,
        )
        assertEquals(
            "1E-15",
            calculate("0.000000000000001", BinaryOperation.Add, "0")
                .uiState.value.primaryValue,
        )
    }

    @Test
    fun `locale separator is used for entry arithmetic and formatted results`() {
        val viewModel = calculate("1,5", BinaryOperation.Add, "2,25", decimalSeparator = ',')

        assertEquals("3,75", viewModel.uiState.value.primaryValue)
        assertEquals(
            CalculatorUiExpression("1,5", BinaryOperation.Add, "2,25"),
            viewModel.uiState.value.expression,
        )
    }

    @Test
    fun `locale change reformats an in-progress calculation without losing it`() {
        val viewModel = calculatorViewModel()
        viewModel.enter("1.5")
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
        viewModel.enter("2.25")

        viewModel.onDecimalSeparatorChanged(',')

        assertEquals("2,25", viewModel.uiState.value.primaryValue)
        assertEquals(
            CalculatorUiExpression("1,5", BinaryOperation.Add),
            viewModel.uiState.value.expression,
        )
        viewModel.onAction(CalculatorAction.Equals)
        assertEquals("3,75", viewModel.uiState.value.primaryValue)
        assertEquals(
            CalculatorUiExpression("1,5", BinaryOperation.Add, "2,25"),
            viewModel.uiState.value.expression,
        )
    }

    @Test
    fun `negative zero is normalized in completed results`() {
        val viewModel = calculate("0", BinaryOperation.Subtract, "1")
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.enter("0")
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("0", viewModel.uiState.value.primaryValue)
    }
}
