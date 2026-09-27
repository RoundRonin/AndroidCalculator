@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import com.anton.calculator.ui.calculator.components.LANDSCAPE_LAYOUT_TAG
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CalculatorLifecycleJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val calculator by lazy { CalculatorRobot(composeRule) }
    private val device by lazy {
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    }

    @Before
    fun startInPortrait() {
        device.setOrientationPortrait()
        composeRule.waitForIdle()
    }

    @After
    fun restoreNaturalOrientation() {
        device.setOrientationNatural()
    }

    @Test
    fun partialCalculationSurvivesActivityRecreationAndCanBeCompleted() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")

        composeRule.activityRule.scenario.recreate()

        calculator
            .assertPendingExpression("12", R.string.add_label)
            .assertEditingValue("3")
            .equals()
            .assertCompletedExpression("12", R.string.add_label, "3")
            .assertResult("15")
    }

    @Test
    fun partialCalculationSurvivesRealRotationAndCanBeCompleted() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")

        device.setOrientationLandscape()
        composeRule.waitForIdle()

        composeRule.onNodeWithTag(LANDSCAPE_LAYOUT_TAG).assertIsDisplayed()
        calculator
            .assertPendingExpression("12", R.string.add_label)
            .assertEditingValue("3")
            .assertEveryKeyIsDisplayed()
            .equals()
            .assertCompletedExpression("12", R.string.add_label, "3")
            .assertResult("15")
    }
}
