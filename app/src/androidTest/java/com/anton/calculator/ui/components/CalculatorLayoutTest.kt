@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorUiExpression
import com.anton.calculator.ui.CalculatorUiState
import com.anton.calculator.ui.theme.CalculatorTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CalculatorLayoutTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun portraitConstraintsPlaceTheDisplayAboveTheKeypad() {
        composeRule.setContent {
            Box(modifier = Modifier.size(width = 360.dp, height = 720.dp)) {
                CalculatorScreen(state = CalculatorUiState(), onAction = {})
            }
        }

        composeRule.onNodeWithTag(PORTRAIT_LAYOUT_TAG).assertIsDisplayed()
        val displayBounds = composeRule.onNodeWithTag(DISPLAY_REGION_TAG)
            .fetchSemanticsNode().boundsInRoot
        val keypadBounds = composeRule.onNodeWithTag(KEYPAD_TAG)
            .fetchSemanticsNode().boundsInRoot
        assertTrue(displayBounds.bottom <= keypadBounds.top)
    }

    @Test
    fun compactHeightConstraintsPlaceTheDisplayBesideTheSameKeypad() {
        composeRule.setContent {
            Box(modifier = Modifier.size(width = 800.dp, height = 360.dp)) {
                CalculatorScreen(state = CalculatorUiState(), onAction = {})
            }
        }

        composeRule.onNodeWithTag(LANDSCAPE_LAYOUT_TAG).assertIsDisplayed()
        val displayBounds = composeRule.onNodeWithTag(DISPLAY_REGION_TAG)
            .fetchSemanticsNode().boundsInRoot
        val keypadBounds = composeRule.onNodeWithTag(KEYPAD_TAG)
            .fetchSemanticsNode().boundsInRoot
        assertTrue(displayBounds.right <= keypadBounds.left)
        listOf("C", "CE", "÷", "×", "−", "+", "=", ".").forEach { label ->
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
        composeRule.onAllNodesWithText("0").assertCountEquals(2)
        (1..9).forEach { digit ->
            composeRule.onNodeWithText(digit.toString()).assertIsDisplayed()
        }
    }

    @Test
    fun expandedWindowCentersAndCapsTheWorkingWidth() {
        composeRule.setContent {
            Box(
                modifier = Modifier
                    .size(width = 1000.dp, height = 800.dp)
                    .testTag(EXPANDED_TEST_CONTAINER_TAG),
            ) {
                CalculatorScreen(state = CalculatorUiState(), onAction = {})
            }
        }

        val bounds = composeRule.onNodeWithTag(PORTRAIT_LAYOUT_TAG)
            .fetchSemanticsNode().boundsInRoot
        val containerBounds = composeRule.onNodeWithTag(EXPANDED_TEST_CONTAINER_TAG)
            .fetchSemanticsNode().boundsInRoot
        val maxWidth = with(composeRule.density) { 520.dp.toPx() }
        composeRule.runOnIdle {
            assertTrue(bounds.width <= maxWidth)
            assertTrue(kotlin.math.abs(bounds.center.x - containerBounds.center.x) <= 1f)
        }
    }

    @Test
    fun everyKeyMeetsTheMinimumTouchTarget() {
        composeRule.setContent {
            Box(modifier = Modifier.size(width = 360.dp, height = 720.dp)) {
                CalculatorScreen(state = CalculatorUiState(), onAction = {})
            }
        }

        val minimumTarget = with(composeRule.density) { 48.dp.toPx() }
        composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes().forEach { node ->
            assertTrue(node.boundsInRoot.width >= minimumTarget)
            assertTrue(node.boundsInRoot.height >= minimumTarget)
        }
    }

    @Test
    fun darkFallbackThemeRendersTheEssentialControls() {
        composeRule.setContent {
            Box(modifier = Modifier.size(width = 720.dp, height = 360.dp)) {
                CalculatorTheme(darkTheme = true, dynamicColor = false) {
                    CalculatorScreen(state = CalculatorUiState(), onAction = {})
                }
            }
        }

        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertIsDisplayed()
        composeRule.onNodeWithText("CE").assertIsDisplayed()
        composeRule.onNodeWithText("=").assertIsDisplayed()
    }

    @Test
    fun lightFallbackThemeRendersTheEssentialControls() {
        composeRule.setContent {
            Box(modifier = Modifier.size(width = 720.dp, height = 360.dp)) {
                CalculatorTheme(darkTheme = false, dynamicColor = false) {
                    CalculatorScreen(state = CalculatorUiState(), onAction = {})
                }
            }
        }

        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertIsDisplayed()
        composeRule.onNodeWithText("CE").assertIsDisplayed()
        composeRule.onNodeWithText("=").assertIsDisplayed()
    }

    @Test
    fun twoHundredPercentFontScaleKeepsDisplayAndControlsUsable() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.FontScale(2f)) {
                Box(modifier = Modifier.size(width = 360.dp, height = 720.dp)) {
                    CalculatorTheme(darkTheme = false, dynamicColor = false) {
                        CalculatorScreen(
                            state = CalculatorUiState(
                                primaryValue = "123456789012345",
                                expression = CalculatorUiExpression(
                                    "123456789012",
                                    BinaryOperation.Add,
                                ),
                            ),
                            onAction = actions::add,
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assertIsDisplayed()
        composeRule.onNodeWithTag(SECONDARY_DISPLAY_TAG).assertIsDisplayed()
        listOf("C", "CE", "÷", "×", "−", "+", "=").forEach { label ->
            composeRule.onNodeWithText(label).assertIsDisplayed()
        }
        composeRule.onNodeWithText(".").assertIsDisplayed()
        (0..9).forEach { digit ->
            composeRule.onNodeWithText(digit.toString()).assertIsDisplayed()
        }
        val minimumTarget = with(composeRule.density) { 48.dp.toPx() }
        val keys = composeRule.onAllNodes(hasClickAction()).fetchSemanticsNodes()
        keys.forEach { key ->
            assertTrue(key.boundsInRoot.width >= minimumTarget)
            assertTrue(key.boundsInRoot.height >= minimumTarget)
        }
        keys.forEachIndexed { index, firstKey ->
            keys.drop(index + 1).forEach { secondKey ->
                val first = firstKey.boundsInRoot
                val second = secondKey.boundsInRoot
                assertTrue(
                    first.right <= second.left ||
                        second.right <= first.left ||
                        first.bottom <= second.top ||
                        second.bottom <= first.top,
                )
            }
        }

        listOf("1", "+", "2", "=").forEach { label ->
            composeRule.onNodeWithText(label).performClick()
        }
        assertEquals(
            listOf(
                CalculatorAction.Digit(1),
                CalculatorAction.SelectOperation(BinaryOperation.Add),
                CalculatorAction.Digit(2),
                CalculatorAction.Equals,
            ),
            actions,
        )
    }
}

private const val EXPANDED_TEST_CONTAINER_TAG = "expandedTestContainer"
