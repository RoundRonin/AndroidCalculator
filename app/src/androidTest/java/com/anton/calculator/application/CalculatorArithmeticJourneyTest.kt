@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import org.junit.Rule
import org.junit.Test

class CalculatorArithmeticJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val calculator by lazy { CalculatorRobot(composeRule) }

    @Test
    fun completedResultBecomesTheFirstOperandOfTheNextCalculation() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")
            .equals()
            .assertCompletedExpression("12", R.string.add_label, "3")
            .assertResult("15")
            .selectOperation(R.string.multiply_label)
            .enter("2")
            .equals()
            .assertCompletedExpression("15", R.string.multiply_label, "2")
            .assertResult("30")
    }

    @Test
    fun userCalculatesWithDecimalOperands() {
        val separator = calculator.decimalSeparator
        val firstOperand = "1${separator}5"
        val secondOperand = "2${separator}25"

        calculator
            .enter(firstOperand)
            .selectOperation(R.string.add_label)
            .enter(secondOperand)
            .equals()
            .assertCompletedExpression(firstOperand, R.string.add_label, secondOperand)
            .assertResult("3${separator}75")
    }
}
