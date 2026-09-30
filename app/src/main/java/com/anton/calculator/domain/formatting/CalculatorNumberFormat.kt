package com.anton.calculator.domain.formatting

import com.anton.calculator.domain.calculation.state.CalculatorState
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

internal fun Double.toCalculatorText(): String {
    if (this == 0.0) return CalculatorState.ZERO
    val rounded = BigDecimal.valueOf(this)
        .round(MathContext(RESULT_SIGNIFICANT_DIGITS, RoundingMode.HALF_UP))
        .stripTrailingZeros()
    val plainText = rounded.toPlainString()
    return plainText.takeIf { it.length <= MAX_PLAIN_RESULT_CHARACTERS }
        ?: rounded.toString()
}

internal fun String.appendCalculatorDigit(digit: Int): String? {
    val candidate = when (this) {
        CalculatorState.ZERO -> digit.toString()
        CalculatorState.NEGATIVE_ZERO -> "-$digit"
        else -> this + digit
    }
    return candidate.takeIf {
        it.length <= MAX_INPUT_CHARACTERS && it.significantDigitCount() <= MAX_INPUT_DIGITS
    }
}

private fun String.significantDigitCount(): Int =
    filter(Char::isDigit).dropWhile { digit -> digit == '0' }.length

private const val MAX_INPUT_DIGITS = 15
private const val MAX_INPUT_CHARACTERS = 32
private const val RESULT_SIGNIFICANT_DIGITS = 12
private const val MAX_PLAIN_RESULT_CHARACTERS = 16
