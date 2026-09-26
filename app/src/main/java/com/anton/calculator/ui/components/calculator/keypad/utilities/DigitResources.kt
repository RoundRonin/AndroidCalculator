package com.anton.calculator.ui.components.calculator.keypad.utilities

import com.anton.calculator.R

import androidx.annotation.StringRes

@get:StringRes
internal val Int.labelResource: Int
    get() = when (this) {
        0 -> R.string.digit_0_label
        1 -> R.string.digit_1_label
        2 -> R.string.digit_2_label
        3 -> R.string.digit_3_label
        4 -> R.string.digit_4_label
        5 -> R.string.digit_5_label
        6 -> R.string.digit_6_label
        7 -> R.string.digit_7_label
        8 -> R.string.digit_8_label
        9 -> R.string.digit_9_label
        else -> error("Unsupported calculator digit: $this")
    }
