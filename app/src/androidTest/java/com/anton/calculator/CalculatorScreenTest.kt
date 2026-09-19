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
}
