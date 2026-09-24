@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator

import androidx.compose.foundation.text.selection.SelectionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CalculatorScreenTest {
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
            Box(modifier = Modifier.size(width = 1000.dp, height = 800.dp)) {
                CalculatorScreen(state = CalculatorUiState(), onAction = {})
            }
        }

        val bounds = composeRule.onNodeWithTag(PORTRAIT_LAYOUT_TAG)
            .fetchSemanticsNode().boundsInRoot
        val maxWidth = with(composeRule.density) { 520.dp.toPx() }
        val expectedCenter = with(composeRule.density) { 500.dp.toPx() }
        composeRule.runOnIdle {
            assertTrue(bounds.width <= maxWidth)
            assertTrue(kotlin.math.abs(bounds.center.x - expectedCenter) <= 1f)
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
        composeRule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.FontScale(2f)) {
                Box(modifier = Modifier.size(width = 360.dp, height = 720.dp)) {
                    CalculatorTheme(darkTheme = false, dynamicColor = false) {
                        CalculatorScreen(
                            state = CalculatorUiState(
                                primaryValue = "123456789012345",
                                secondaryExpression = "123456789012 +",
                            ),
                            onAction = {},
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
    }

    @Test
    fun onlyThePrimaryDisplayParticipatesInSystemTextSelection() {
        val selectionState = SelectionState()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "15",
                    secondaryExpression = "12 + 3 =",
                ),
                onAction = {},
                primarySelectionState = selectionState,
            )
        }

        composeRule.runOnIdle {
            assertEquals(
                listOf("15"),
                selectionState.getSelectableTexts().map { text -> text.text },
            )
        }
    }

    @Test
    fun initialStateShowsZeroAndTheWholeNumberKeypad() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = {},
            )
        }

        composeRule.onAllNodesWithText("0").assertCountEquals(2)
        composeRule.onAllNodesWithText("0")[0].assertIsDisplayed()
        composeRule.onAllNodesWithText("0")[1].assertIsDisplayed()
        (1..9).forEach { digit ->
            composeRule.onNodeWithText(digit.toString()).assertIsDisplayed()
        }
        composeRule.onNodeWithText("C").assertIsDisplayed()
    }

    @Test
    fun enteredNumberIsRendered() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(primaryValue = "42"),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("42").assertIsDisplayed()
    }

    @Test
    fun digitButtonEmitsItsDigitAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("7").performClick()

        assertEquals(listOf(CalculatorAction.Digit(7)), actions)
    }

    @Test
    fun clearButtonEmitsClearAllAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(primaryValue = "42"),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("C").performClick()

        assertEquals(listOf(CalculatorAction.ClearAll), actions)
    }

    @Test
    fun clearEntryButtonEmitsClearEntryAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "3",
                    secondaryExpression = "12 +",
                ),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText("CE").performClick()

        assertEquals(listOf(CalculatorAction.ClearEntry), actions)
    }

    @Test
    fun operationAndEqualsButtonsEmitTheirActions() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(),
                onAction = actions::add,
            )
        }

        listOf("+", "−", "×", "÷", "=").forEach { label ->
            composeRule.onNodeWithText(label).performClick()
        }

        assertEquals(
            listOf(
                CalculatorAction.SelectOperation(BinaryOperation.Add),
                CalculatorAction.SelectOperation(BinaryOperation.Subtract),
                CalculatorAction.SelectOperation(BinaryOperation.Multiply),
                CalculatorAction.SelectOperation(BinaryOperation.Divide),
                CalculatorAction.Equals,
            ),
            actions,
        )
    }

    @Test
    fun decimalButtonUsesConfiguredSeparatorAndEmitsDecimalAction() {
        val actions = mutableListOf<CalculatorAction>()
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(decimalSeparator = ','),
                onAction = actions::add,
            )
        }

        composeRule.onNodeWithText(",").performClick()

        assertEquals(listOf(CalculatorAction.Decimal), actions)
    }

    @Test
    fun pendingCalculationRendersBothDisplayLines() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "3",
                    secondaryExpression = "12 +",
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("12 +").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()
    }

    @Test
    fun completedCalculationRendersExpressionAndResult() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "15",
                    secondaryExpression = "12 + 3 =",
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("12 + 3 =").assertIsDisplayed()
        composeRule.onNodeWithText("15").assertIsDisplayed()
    }

    @Test
    fun errorStateRendersItsExpressionAndVisibleError() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    primaryValue = "Error",
                    secondaryExpression = "1 ÷ 0 =",
                    displayStatus = CalculatorDisplayStatus.Error,
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithText("1 ÷ 0 =").assertIsDisplayed()
        composeRule.onNodeWithText("Error").assertIsDisplayed()
    }
}
