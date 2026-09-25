package com.anton.calculator

data class CalculatorUiState(
    val primaryValue: String = "0",
    val secondaryExpression: String = "",
    val decimalSeparator: Char = '.',
    val displayStatus: CalculatorDisplayStatus = CalculatorDisplayStatus.Editing,
    val selectedOperation: BinaryOperation? = null,
)

enum class CalculatorDisplayStatus {
    Editing,
    Result,
    Error,
}
