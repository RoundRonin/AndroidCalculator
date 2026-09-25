@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.anton.calculator.ui.components.PRIMARY_DISPLAY_TAG
import org.junit.Rule
import org.junit.Test

class CalculatorActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun partialCalculationSurvivesActivityRecreationAndCanBeCompleted() {
        press("1", "2", "+", "3")

        composeRule.activityRule.scenario.recreate()

        composeRule.onNodeWithText("12 +").assertIsDisplayed()
        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertIsDisplayed()
        press("=")
        composeRule.onNodeWithText("12 + 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("15").assertIsDisplayed()
    }

    @Test
    fun userChainsTwelvePlusThreeThenMultipliesByTwo() {
        press("1", "2", "+", "3", "=")

        composeRule.onNodeWithText("12 + 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("15").assertIsDisplayed()

        press("×", "2", "=")

        composeRule.onNodeWithText("15 × 2 =").assertIsDisplayed()
        composeRule.onNodeWithText("30").assertIsDisplayed()
    }

    @Test
    fun userReplacesAdditionWithMultiplication() {
        press("1", "2", "+", "×", "3", "=")

        composeRule.onNodeWithText("12 × 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("36").assertIsDisplayed()
    }

    @Test
    fun userRecoversFromDivisionByZeroAndCalculatesAgain() {
        press("1", "÷", "0", "=")

        composeRule.onNodeWithText("1 ÷ 0 =").assertIsDisplayed()
        composeRule.onNodeWithText("Error").assertIsDisplayed()

        press("CE", "6", "÷", "2", "=")

        composeRule.onNodeWithText("6 ÷ 2 =").assertIsDisplayed()
        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertTextEquals("3")
    }

    private fun press(vararg labels: String) {
        labels.forEach { label ->
            composeRule.onNode(hasText(label) and hasClickAction()).performClick()
        }
    }
}
