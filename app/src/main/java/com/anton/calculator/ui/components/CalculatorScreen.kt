package com.anton.calculator.ui.components

import com.anton.calculator.domain.CalculatorAction
import com.anton.calculator.ui.CalculatorUiState

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

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
            val landscape = maxHeight < COMPACT_HEIGHT_BREAKPOINT && maxWidth > maxHeight
            if (landscape) {
                LandscapeCalculator(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .widthIn(max = MAX_CONTENT_WIDTH)
                        .fillMaxSize()
                        .testTag(LANDSCAPE_LAYOUT_TAG),
                )
            } else {
                PortraitCalculator(
                    state = state,
                    onAction = onAction,
                    modifier = Modifier
                        .widthIn(max = MAX_CONTENT_WIDTH)
                        .fillMaxSize()
                        .testTag(PORTRAIT_LAYOUT_TAG),
                )
            }
        }
    }
}

@Composable
private fun PortraitCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        CalculatorDisplay(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
        CalculatorKeypad(
            decimalSeparator = state.decimalSeparator,
            selectedOperation = state.selectedOperation,
            onAction = onAction,
            keyShape = CircleShape,
            squareKeys = true,
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = MAX_PORTRAIT_KEYPAD_WIDTH),
        )
    }
}

@Composable
private fun LandscapeCalculator(
    state: CalculatorUiState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CalculatorDisplay(
            state = state,
            modifier = Modifier
                .fillMaxHeight()
                .weight(2f),
        )
        CalculatorKeypad(
            decimalSeparator = state.decimalSeparator,
            selectedOperation = state.selectedOperation,
            onAction = onAction,
            keyShape = RoundedCornerShape(percent = 50),
            squareKeys = false,
            modifier = Modifier
                .fillMaxHeight()
                .weight(3f),
        )
    }
}

internal const val PRIMARY_DISPLAY_TAG = "primaryDisplay"
internal const val SECONDARY_DISPLAY_TAG = "secondaryDisplay"
internal const val PORTRAIT_LAYOUT_TAG = "portraitLayout"
internal const val LANDSCAPE_LAYOUT_TAG = "landscapeLayout"
internal const val DISPLAY_REGION_TAG = "displayRegion"
internal const val KEYPAD_TAG = "keypad"

private val COMPACT_HEIGHT_BREAKPOINT = 480.dp
private val MAX_CONTENT_WIDTH = 520.dp
private val MAX_PORTRAIT_KEYPAD_WIDTH = 400.dp
