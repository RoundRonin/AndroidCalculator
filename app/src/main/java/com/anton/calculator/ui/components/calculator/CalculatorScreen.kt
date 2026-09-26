package com.anton.calculator.ui.components.calculator

import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorUiState

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
import androidx.compose.ui.unit.dp
import com.anton.calculator.ui.components.calculator.layout.LandscapeCalculator
import com.anton.calculator.ui.components.calculator.layout.PortraitCalculator
import com.anton.calculator.ui.components.calculator.display.CompactHeightBreakpoint
import com.anton.calculator.ui.components.calculator.display.MaxContentWidth

@Composable
internal fun CalculatorScreen(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize()) {
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
                        .widthIn(max = MaxContentWidth)
                        .fillMaxSize()
                        .testTag(LANDSCAPE_LAYOUT_TAG),
                )
            } else {
                PortraitCalculator(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .widthIn(max = MaxContentWidth)
                        .fillMaxSize()
                        .testTag(PORTRAIT_LAYOUT_TAG),
                )
            }
        }
    }
}
