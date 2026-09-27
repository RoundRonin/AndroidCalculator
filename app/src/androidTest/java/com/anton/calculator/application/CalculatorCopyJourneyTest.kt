@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.application

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Resources
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.onElement
import androidx.test.uiautomator.textAsString
import com.anton.calculator.MainActivity
import com.anton.calculator.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class CalculatorCopyJourneyTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private val calculator by lazy { CalculatorRobot(composeRule) }

    @Test
    fun userCopiesTheCompletedResultThroughTheSystemSelectionToolbar() {
        calculator
            .enter("12")
            .selectOperation(R.string.add_label)
            .enter("3")
            .equals()
            .assertResult("15")
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("stale", "stale"))

        calculator.longPressResult("15")

        val copyResource = Resources.getSystem().getIdentifier("copy", "string", "android")
        assertNotEquals("Android must expose its localized Copy action", 0, copyResource)
        val copyLabel = Resources.getSystem().getString(copyResource)
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
            .onElement { textAsString() == copyLabel }
            .click()

        composeRule.waitUntil(timeoutMillis = 5_000) {
            clipboard.primaryClip
                ?.getItemAt(0)
                ?.coerceToText(context)
                ?.toString() == "15"
        }
        assertEquals("15", clipboard.primaryClip?.getItemAt(0)?.coerceToText(context).toString())
    }
}
