package com.anton.calculator.domain

internal enum class BinaryOperation {
    Add {
        override fun apply(left: Double, right: Double) = left + right
    },
    Subtract {
        override fun apply(left: Double, right: Double) = left - right
    },
    Multiply {
        override fun apply(left: Double, right: Double) = left * right
    },
    Divide {
        override fun apply(left: Double, right: Double) = left / right
    },
    ;

    abstract fun apply(left: Double, right: Double): Double
}
