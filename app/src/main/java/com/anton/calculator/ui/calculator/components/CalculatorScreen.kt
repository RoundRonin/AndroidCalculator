package com.anton.calculator.ui.calculator.components

import com.anton.calculator.domain.calculation.CalculatorAction
import com.anton.calculator.ui.calculator.presentation.CalculatorUiState

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import com.anton.calculator.ui.calculator.components.layout.CompactHeightBreakpoint
import com.anton.calculator.ui.calculator.components.layout.LandscapeCalculator
import com.anton.calculator.ui.calculator.components.layout.MaxLandscapeContentWidth
import com.anton.calculator.ui.calculator.components.layout.MaxPortraitContentWidth
import com.anton.calculator.ui.calculator.components.layout.PortraitCalculator

@Composable
internal fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .semantics { testTagsAsResourceId = true },
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            val landscape = maxHeight < CompactHeightBreakpoint && maxWidth > maxHeight
            if (landscape) {
                LandscapeCalculator(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .widthIn(max = MaxLandscapeContentWidth)
                        .fillMaxSize()
                        .testTag(LANDSCAPE_LAYOUT_TAG),
                )
            } else {
                PortraitCalculator(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .widthIn(max = MaxPortraitContentWidth)
                        .fillMaxSize()
                        .testTag(PORTRAIT_LAYOUT_TAG),
                )
            }
        }
    }
}
