@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class CalculatorActivityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun userChainsTwelvePlusThreeThenMultipliesByTwo() {
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("2").performClick()
        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("3").performClick()
        composeRule.onNodeWithText("=").performClick()

        composeRule.onNodeWithText("12 + 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("15").assertIsDisplayed()

        composeRule.onNodeWithText("×").performClick()
        composeRule.onNodeWithText("2").performClick()
        composeRule.onNodeWithText("=").performClick()

        composeRule.onNodeWithText("15 × 2 =").assertIsDisplayed()
        composeRule.onNodeWithText("30").assertIsDisplayed()
    }

    @Test
    fun userReplacesAdditionWithMultiplication() {
        composeRule.onNodeWithText("1").performClick()
        composeRule.onNodeWithText("2").performClick()
        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("×").performClick()
        composeRule.onNodeWithText("3").performClick()
        composeRule.onNodeWithText("=").performClick()

        composeRule.onNodeWithText("12 × 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("36").assertIsDisplayed()
    }
}
