package com.anton.calculator

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.testing.viewModelScenario
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorViewModelTest {
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
    fun `partial calculation survives view model recreation and remains continuable`() {
        viewModelScenario {
            CalculatorViewModel(createSavedStateHandle(), '.')
        }.use { scenario ->
            scenario.viewModel.enter("12")
            scenario.viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Add))
            scenario.viewModel.enter("3")
            val stateBeforeRecreation = scenario.viewModel.uiState.value

            scenario.recreate()

            assertEquals(stateBeforeRecreation, scenario.viewModel.uiState.value)
            scenario.viewModel.onAction(CalculatorAction.Equals)
            assertEquals("15", scenario.viewModel.uiState.value.primaryValue)
            assertEquals("12 + 3 =", scenario.viewModel.uiState.value.secondaryExpression)
        }
    }

    @Test
    fun `completed result survives view model recreation and remains reusable`() {
        viewModelScenario {
            CalculatorViewModel(createSavedStateHandle(), '.')
        }.use { scenario ->
            scenario.viewModel.enter("1")
            scenario.viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
            scenario.viewModel.enter("3")
            scenario.viewModel.onAction(CalculatorAction.Equals)
            val stateBeforeRecreation = scenario.viewModel.uiState.value

            scenario.recreate()

            assertEquals(stateBeforeRecreation, scenario.viewModel.uiState.value)
            scenario.viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
            scenario.viewModel.enter("3")
            scenario.viewModel.onAction(CalculatorAction.Equals)
            assertEquals("1", scenario.viewModel.uiState.value.primaryValue)
        }
    }

    @Test
    fun `error survives view model recreation and keeps recovery behaviour`() {
        viewModelScenario {
            CalculatorViewModel(createSavedStateHandle(), '.')
        }.use { scenario ->
            scenario.viewModel.enter("1")
            scenario.viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Divide))
            scenario.viewModel.enter("0")
            scenario.viewModel.onAction(CalculatorAction.Equals)
            val stateBeforeRecreation = scenario.viewModel.uiState.value

            scenario.recreate()

            assertEquals(stateBeforeRecreation, scenario.viewModel.uiState.value)
            scenario.viewModel.onAction(CalculatorAction.Digit(7))
            assertEquals(CalculatorUiState(primaryValue = "7"), scenario.viewModel.uiState.value)
        }
    }

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
        assertEquals("4 +", viewModel.uiState.value.secondaryExpression)
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

        assertEquals(32, viewModel.uiState.value.primaryValue.length)
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
        assertEquals("1,5 + 2,25 =", viewModel.uiState.value.secondaryExpression)
    }

    @Test
    fun `negative zero is normalized in completed results`() {
        val viewModel = calculate("0", BinaryOperation.Subtract, "1")
        viewModel.onAction(CalculatorAction.SelectOperation(BinaryOperation.Multiply))
        viewModel.enter("0")
        viewModel.onAction(CalculatorAction.Equals)

        assertEquals("0", viewModel.uiState.value.primaryValue)
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

    private fun calculate(
        left: String,
        operation: BinaryOperation,
        right: String,
        decimalSeparator: Char = '.',
    ): CalculatorViewModel = calculatorViewModel(decimalSeparator).apply {
        enter(left)
        onAction(CalculatorAction.SelectOperation(operation))
        enter(right)
        onAction(CalculatorAction.Equals)
    }

    private fun errorViewModel(decimalSeparator: Char = '.'): CalculatorViewModel =
        calculate("1", BinaryOperation.Divide, "0", decimalSeparator)

    private fun CalculatorViewModel.enter(value: String) {
        value.forEach { character ->
            when {
                character.isDigit() -> onAction(CalculatorAction.Digit(character.digitToInt()))
                character == uiState.value.decimalSeparator -> onAction(CalculatorAction.Decimal)
                else -> error("Unsupported calculator test input: $character")
            }
        }
    }
}

private fun calculatorViewModel(decimalSeparator: Char = '.'): CalculatorViewModel =
    CalculatorViewModel(SavedStateHandle(), decimalSeparator)
