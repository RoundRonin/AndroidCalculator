@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.anton.calculator.ui.calculator.components.PRIMARY_DISPLAY_TAG
import org.junit.Rule
import org.junit.Test
import java.text.DecimalFormatSymbols

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
    fun userCalculatesWithDecimalOperands() {
        val decimalSeparator = DecimalFormatSymbols.getInstance(currentLocale()).decimalSeparator
        val firstOperand = "1${decimalSeparator}5"
        val secondOperand = "2${decimalSeparator}25"
        press(
            localizedString(R.string.digit_1_label),
            localizedString(R.string.decimal_label, decimalSeparator),
            localizedString(R.string.digit_5_label),
            localizedString(R.string.add_label),
            localizedString(R.string.digit_2_label),
            localizedString(R.string.decimal_label, decimalSeparator),
            localizedString(R.string.digit_2_label),
            localizedString(R.string.digit_5_label),
            localizedString(R.string.equals_label),
        )

        composeRule.onNodeWithText(
            localizedString(
                R.string.completed_expression_format,
                firstOperand,
                localizedString(R.string.add_label),
                secondOperand,
                localizedString(R.string.equals_label),
            ),
        ).assertIsDisplayed()
        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG)
            .assertTextEquals("3${decimalSeparator}75")
    }

    @Test
    fun userCorrectsTheSecondOperandWithClearEntry() {
        press(
            localizedString(R.string.digit_1_label),
            localizedString(R.string.digit_2_label),
            localizedString(R.string.add_label),
            localizedString(R.string.digit_3_label),
            localizedString(R.string.digit_4_label),
            localizedString(R.string.clear_entry_label),
            localizedString(R.string.digit_5_label),
            localizedString(R.string.equals_label),
        )

        composeRule.onNodeWithText(
            localizedString(
                R.string.completed_expression_format,
                "12",
                localizedString(R.string.add_label),
                "5",
                localizedString(R.string.equals_label),
            ),
        ).assertIsDisplayed()
        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertTextEquals("17")
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

    private fun currentLocale() =
        InstrumentationRegistry.getInstrumentation().targetContext.resources.configuration.locales[0]

    private fun localizedString(
        @StringRes resourceId: Int,
        vararg formatArguments: Any,
    ): String = InstrumentationRegistry.getInstrumentation().targetContext.getString(
        resourceId,
        *formatArguments,
    )
}
