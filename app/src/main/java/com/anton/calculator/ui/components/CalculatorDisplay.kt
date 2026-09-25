package com.anton.calculator.ui.components

import com.anton.calculator.R
import com.anton.calculator.ui.CalculatorDisplayStatus
import com.anton.calculator.ui.CalculatorUiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.text.selection.SelectionState
import androidx.compose.foundation.text.selection.rememberSelectionState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
internal fun CalculatorDisplay(
    state: CalculatorUiState,
    modifier: Modifier = Modifier,
    primarySelectionState: SelectionState = rememberSelectionState(),
) {
    val errorDescription = stringResource(R.string.calculation_error_description)
    val resultDescription = stringResource(R.string.result_description, state.primaryValue)
    Column(
        modifier = modifier
            .testTag(DISPLAY_REGION_TAG)
            .semantics { isTraversalGroup = true },
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
    ) {
        if (state.secondaryExpression.isNotEmpty()) {
            Text(
                text = state.secondaryExpression,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(SECONDARY_DISPLAY_TAG)
                    .semantics { traversalIndex = 0f },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 14.sp,
                    maxFontSize = 22.sp,
                    stepSize = 1.sp,
                ),
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
            )
        }
        SelectionContainer(
            state = primarySelectionState,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = state.primaryValue,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(PRIMARY_DISPLAY_TAG)
                    .semantics {
                        traversalIndex = 1f
                        when (state.displayStatus) {
                            CalculatorDisplayStatus.Result -> {
                                contentDescription = resultDescription
                                liveRegion = LiveRegionMode.Polite
                            }
                            CalculatorDisplayStatus.Error -> {
                                contentDescription = errorDescription
                                error(errorDescription)
                                liveRegion = LiveRegionMode.Polite
                            }
                            CalculatorDisplayStatus.Editing -> Unit
                        }
                    },
                style = MaterialTheme.typography.displayLarge,
                autoSize = TextAutoSize.StepBased(
                    minFontSize = 18.sp,
                    maxFontSize = 64.sp,
                    stepSize = 2.sp,
                ),
                textAlign = TextAlign.End,
                maxLines = 1,
            )
        }
    }
}
