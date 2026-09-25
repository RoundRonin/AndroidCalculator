package com.anton.calculator.ui

import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.testing.viewModelScenario
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CalculatorPersistenceTest {
    @Test
    fun `partial calculation survives view model recreation and remains continuable`() {
        viewModelScenario {
            CalculatorViewModel(createSavedStateHandle())
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
            CalculatorViewModel(createSavedStateHandle())
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
            CalculatorViewModel(createSavedStateHandle())
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
}

