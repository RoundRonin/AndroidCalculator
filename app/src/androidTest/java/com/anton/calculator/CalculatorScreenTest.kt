@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class CalculatorScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun initialStateShowsZeroAndTheWholeNumberKeypad() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = {},
            )
        }

        composeRule.onAllNodesWithText("0").assertCountEquals(2)
        composeRule.onAllNodesWithText("0")[0].assertIsDisplayed()
        composeRule.onAllNodesWithText("0")[1].assertIsDisplayed()
        (1..9).forEach { digit ->
            composeRule.onNodeWithText(digit.toString()).assertIsDisplayed()
        }
        composeRule.onNodeWithText("C").assertIsDisplayed()
    }

    @Test
    fun enteredNumberIsRendered() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(primaryValue = "42"),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("42").assertIsDisplayed()
    }

    @Test
    fun digitButtonEmitsItsDigitAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("7").performClick()

        assertEquals(listOf(CalculatorAction.Digit(7)), actions)
    }

    @Test
    fun clearButtonEmitsClearAllAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(primaryValue = "42"),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("C").performClick()

        assertEquals(listOf(CalculatorAction.ClearAll), actions)
    }

    @Test
    fun clearEntryButtonEmitsClearEntryAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "3",
                    secondaryExpression = "12 +",
                ),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("CE").performClick()

        assertEquals(listOf(CalculatorAction.ClearEntry), actions)
    }

    @Test
    fun operationAndEqualsButtonsEmitTheirActions() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = actions::add,
            )
        }

        listOf("+", "−", "×", "÷", "=").forEach { label ->
            composeRule.onNodeWithText(label).performClick()
        }

        assertEquals(
            listOf(
                CalculatorAction.SelectOperation(BinaryOperation.Add),
                CalculatorAction.SelectOperation(BinaryOperation.Subtract),
                CalculatorAction.SelectOperation(BinaryOperation.Multiply),
                CalculatorAction.SelectOperation(BinaryOperation.Divide),
                CalculatorAction.Equals,
            ),
            actions,
        )
    }

    @Test
    fun decimalButtonUsesConfiguredSeparatorAndEmitsDecimalAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(decimalSeparator = ','),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(",").performClick()

        assertEquals(listOf(CalculatorAction.Decimal), actions)
    }

    @Test
    fun pendingCalculationRendersBothDisplayLines() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "3",
                    secondaryExpression = "12 +",
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("12 +").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun completedCalculationRendersExpressionAndResult() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "15",
                    secondaryExpression = "12 + 3 =",
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("12 + 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("15").assertIsDisplayed()
    }

    @Test
    fun errorStateRendersItsExpressionAndVisibleError() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "Error",
                    secondaryExpression = "1 ÷ 0 =",
                    displayStatus = CalculatorDisplayStatus.Error,
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("1 ÷ 0 =").assertIsDisplayed()
        composeRule.onNodeWithText("Error").assertIsDisplayed()
    }
}
