@file:Suppress("DEPRECATION") // The v2 replacement remains alpha in Compose 1.12.

package com.anton.calculator.ui.components

import com.anton.calculator.R
import com.anton.calculator.domain.BinaryOperation
import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorDisplayStatus
import com.anton.calculator.ui.CalculatorUiState

import androidx.compose.foundation.text.selection.SelectionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.FontScale
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class CalculatorSemanticsTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun symbolicAndClearingKeysHaveSpokenMeaningsAndKeepButtonActions() {
        composeRule.setContent {
            CalculatorScreen(state = CalculatorUiState(), onAction = {})
        }

        listOf(
            localizedString(R.string.add_description),
            localizedString(R.string.subtract_description),
            localizedString(R.string.multiply_description),
            localizedString(R.string.divide_description),
            localizedString(R.string.equals_description),
            localizedString(R.string.decimal_description, '.'),
            localizedString(R.string.clear_all_description),
            localizedString(R.string.clear_entry_description),
        ).forEach { description ->
            composeRule.onNodeWithContentDescription(description)
                .assertHasClickAction()
                .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        }
    }

    @Test
    fun pendingOperatorIsSelectedSemantically() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(
                    secondaryExpression = "12 +",
                    selectedOperation = BinaryOperation.Add,
                ),
                onAction = {},
            )
        }

        composeRule.onNodeWithContentDescription(localizedString(R.string.add_description))
            .assertIsSelected()
        composeRule.onNodeWithContentDescription(localizedString(R.string.subtract_description))
            .assertIsNotSelected()
    }

    @Test
    fun errorUsesErrorSemanticsAndPoliteLiveFeedback() {
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

        composeRule.onNodeWithTag(DISPLAY_REGION_TAG).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.IsTraversalGroup, true),
        )
        composeRule.onNodeWithTag(SECONDARY_DISPLAY_TAG).assert(
            SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 0f),
        )
        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.TraversalIndex, 1f))
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.Error,
                    localizedString(R.string.calculation_error_description),
                ),
            )
            .assert(
                SemanticsMatcher.expectValue(
                    SemanticsProperties.LiveRegion,
                    LiveRegionMode.Polite,
                ),
            )
    }

    @Test
    fun digitEntryIsNotALiveRegionAndNoEditableInputExists() {
        composeRule.setContent {
            CalculatorScreen(
                state = CalculatorUiState(primaryValue = "7"),
                onAction = {},
            )
        }

        composeRule.onNodeWithTag(PRIMARY_DISPLAY_TAG).assert(
            SemanticsMatcher.keyNotDefined(SemanticsProperties.LiveRegion),
        )
        composeRule.onAllNodes(hasSetTextAction()).assertCountEquals(0)
    }

    @Test
    fun onlyThePrimaryDisplayParticipatesInSystemTextSelection() {
        val selectionState = SelectionState()
        composeRule.setContent {
            CalculatorDisplay(
                state = CalculatorUiState(
                    primaryValue = "15",
                    secondaryExpression = "12 + 3 =",
                ),
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

    private fun localizedString(resourceId: Int, vararg formatArguments: Any): String =
        InstrumentationRegistry.getInstrumentation().targetContext.resources.getString(
            resourceId,
            *formatArguments,
        )

}

