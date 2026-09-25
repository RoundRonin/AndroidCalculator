@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.junit4.accessibility.enableAccessibilityChecks
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.filters.SdkSuppress
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SdkSuppress(minSdkVersion = 34)
class CalculatorAccessibilityTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun enableAccessibilityChecks() {
        composeRule.enableAccessibilityChecks()
    }

    @Test
    fun calculatorPassesAutomatedAccessibilityChecks() {
        val clearAllDescription = composeRule.activity.getString(R.string.clear_all_description)

        composeRule.onNodeWithContentDescription(clearAllDescription)
            .assertHasClickAction()
            .performClick()
    }
}
