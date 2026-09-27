@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import org.junit.Rule
import org.junit.Test

class CalculatorEditingJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val calculator by lazy { CalculatorRobot(composeRule) }

    @Test
    fun userReplacesAnOperationBeforeEnteringTheSecondOperand() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .selectOperation(R.string.multiply_label)
            .enter("3")
            .equals()
            .assertCompletedExpression("12", R.string.multiply_label, "3")
            .assertResult("36")
    }

    @Test
    fun userCorrectsTheSecondOperandWithClearEntry() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("34")
            .clearEntry()
            .assertPendingExpression("12", R.string.add_label)
            .assertEditingValue("0")
            .enter("5")
            .equals()
            .assertCompletedExpression("12", R.string.add_label, "5")
            .assertResult("17")
    }

    @Test
    fun clearAllResetsAPendingCalculation() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")
            .clearAll()
            .assertEditingValue("0")
            .enter("7")
            .selectOperation(R.string.subtract_label)
            .enter("2")
            .equals()
            .assertResult("5")
    }

    @Test
    fun digitAfterAResultStartsANewCalculation() {
        calculator
            .enter("2")
            .selectOperation(R.string.add_label)
            .enter("3")
            .equals()
            .assertResult("5")
            .enter("7")
            .assertEditingValue("7")
    }

    @Test
    fun userRecoversFromDivisionByZeroAndCalculatesAgain() {
        calculator
            .enter("1")
            .selectOperation(R.string.divide_label)
            .enter("0")
            .equals()
            .assertCompletedExpression("1", R.string.divide_label, "0")
            .assertError()
            .clearEntry()
            .enter("6")
            .selectOperation(R.string.divide_label)
            .enter("2")
            .equals()
            .assertResult("3")
    }
}
