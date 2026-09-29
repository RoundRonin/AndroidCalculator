@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class CalculatorCheckerCompatibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun resultIsDiscoverableByResourceIdAfterCalculation() {
        CalculatorRobot(composeRule)
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")
            .equals()

        composeRule.waitForIdle()

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        val result = device.wait(Until.findObject(By.res("result")), 5_000)

        assertNotNull(result)
        assertEquals("15", result.text)
    }
}
