@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import androidx.annotation.StringRes
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class CalculatorOperationJourneyTest(
    @Suppress("UNUSED_PARAMETER") caseName: String,
    @param:StringRes private val operationLabel: Int,
    private val leftOperand: String,
    private val rightOperand: String,
    private val expectedResult: String,
) {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val calculator by lazy { CalculatorRobot(composeRule) }

    @Test
    fun userCompletesOperationThroughTheRenderedKeypad() {
        calculator
            .enter(leftOperand)
            .selectOperation(operationLabel)
            .enter(rightOperand)
            .equals()
            .assertCompletedExpression(leftOperand, operationLabel, rightOperand)
            .assertResult(expectedResult)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun operationCases(): List<Array<Any>> = listOf(
            arrayOf("addition", R.string.add_label, "2", "3", "5"),
            arrayOf("subtraction", R.string.subtract_label, "7", "9", "-2"),
            arrayOf("multiplication", R.string.multiply_label, "4", "6", "24"),
            arrayOf("division", R.string.divide_label, "8", "2", "4"),
        )
    }
}
