package com.anton.calculator.ui.components

import com.anton.calculator.R
import com.anton.calculator.domain.BinaryOperation

import androidx.annotation.StringRes

@get:StringRes
internal val BinaryOperation.labelResource: Int
    get() = when (this) {
        BinaryOperation.Add -> R.string.add_label
        BinaryOperation.Subtract -> R.string.subtract_label
        BinaryOperation.Multiply -> R.string.multiply_label
        BinaryOperation.Divide -> R.string.divide_label
    }

@get:StringRes
internal val BinaryOperation.descriptionResource: Int
    get() = when (this) {
        BinaryOperation.Add -> R.string.add_description
        BinaryOperation.Subtract -> R.string.subtract_description
        BinaryOperation.Multiply -> R.string.multiply_description
        BinaryOperation.Divide -> R.string.divide_description
    }
