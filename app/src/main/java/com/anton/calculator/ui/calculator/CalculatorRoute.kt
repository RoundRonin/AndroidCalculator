package com.anton.calculator.ui.calculator

import com.anton.calculator.ui.calculator.components.CalculatorScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.DecimalFormatSymbols

@Composable
internal fun CalculatorRoute(
    viewModel: CalculatorViewModel = viewModel(),
) {
    val locale = LocalConfiguration.current.locales[0]
    val decimalSeparator = remember(locale) {
        DecimalFormatSymbols.getInstance(locale).decimalSeparator
    }
    LaunchedEffect(decimalSeparator) {
        viewModel.onDecimalSeparatorChanged(decimalSeparator)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    CalculatorScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
