package com.anton.calculator.application

import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import java.text.DecimalFormatSymbols

internal typealias CalculatorComposeRule =
    AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>

internal class CalculatorRobot(
    private val composeRule: CalculatorComposeRule,
) {
    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    val decimalSeparator: Char
        get() = DecimalFormatSymbols.getInstance(
            context.resources.configuration.locales[0],
        ).decimalSeparator

    fun enter(number: String) = apply {
        number.forEach { character ->
            when {
                character.isDigit() -> tap(digitLabel(character.digitToInt()))
                character == decimalSeparator -> tap(R.string.decimal_label, decimalSeparator)
                else -> error("Unsupported calculator journey input: $character")
            }
        }
    }

    fun selectOperation(@StringRes label: Int) = apply { tap(label) }

    fun equals() = apply { tap(R.string.equals_label) }

    fun clearAll() = apply { tap(R.string.clear_all_label) }

    fun clearEntry() = apply { tap(R.string.clear_entry_label) }

    fun assertResult(expected: String) = apply {
        composeRule.onNodeWithContentDescription(
            string(R.string.result_description, expected),
        ).assertIsDisplayed().assertTextEquals(expected)
    }

    fun longPressResult(expected: String) = apply {
        composeRule.onNodeWithContentDescription(
            string(R.string.result_description, expected),
        ).performTouchInput { longClick() }
    }

    fun assertError() = apply {
        composeRule.onNodeWithContentDescription(
            string(R.string.calculation_error_description),
        ).assertIsDisplayed().assertTextEquals(string(R.string.calculation_error_label))
    }

    fun assertEditingValue(expected: String) = apply {
        composeRule.onNode(hasText(expected) and !hasClickAction())
            .assertIsDisplayed()
            .assertTextEquals(expected)
    }

    fun assertPendingExpression(
        leftOperand: String,
        @StringRes operationLabel: Int,
    ) = apply {
        composeRule.onNodeWithText(
            string(
                R.string.pending_expression_format,
                leftOperand,
                string(operationLabel),
            ),
        ).assertIsDisplayed()
    }

    fun assertCompletedExpression(
        leftOperand: String,
        @StringRes operationLabel: Int,
        rightOperand: String,
    ) = apply {
        composeRule.onNodeWithText(
            string(
                R.string.completed_expression_format,
                leftOperand,
                string(operationLabel),
                rightOperand,
                string(R.string.equals_label),
            ),
        ).assertIsDisplayed()
    }

    fun assertEveryKeyIsDisplayed() = apply {
        (0..9).forEach { digit ->
            composeRule.onNode(hasText(string(digitLabel(digit))) and hasClickAction())
                .assertIsDisplayed()
        }
        listOf(
            R.string.clear_all_label,
            R.string.clear_entry_label,
            R.string.divide_label,
            R.string.multiply_label,
            R.string.subtract_label,
            R.string.add_label,
            R.string.equals_label,
        ).forEach { label ->
            composeRule.onNode(hasText(string(label)) and hasClickAction()).assertIsDisplayed()
        }
        composeRule.onNode(
            hasText(string(R.string.decimal_label, decimalSeparator)) and hasClickAction(),
        ).assertIsDisplayed()
    }

    private fun tap(
        @StringRes label: Int,
        vararg formatArguments: Any,
    ) {
        composeRule.onNode(
            hasText(string(label, *formatArguments)) and hasClickAction(),
        ).performClick()
    }

    private fun string(
        @StringRes resourceId: Int,
        vararg formatArguments: Any,
    ): String = context.getString(resourceId, *formatArguments)

    @StringRes
    private fun digitLabel(digit: Int): Int = when (digit) {
        0 -> R.string.digit_0_label
        1 -> R.string.digit_1_label
        2 -> R.string.digit_2_label
        3 -> R.string.digit_3_label
        4 -> R.string.digit_4_label
        5 -> R.string.digit_5_label
        6 -> R.string.digit_6_label
        7 -> R.string.digit_7_label
        8 -> R.string.digit_8_label
        9 -> R.string.digit_9_label
        else -> error("Unsupported digit: $digit")
    }
}
