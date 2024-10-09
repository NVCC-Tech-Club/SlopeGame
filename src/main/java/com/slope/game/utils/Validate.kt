package com.slope.game.utils

// Our own
object Validate {
    private const val DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified inclusive range of %s to %s";

    @JvmStatic
    fun inclusiveBetween(min: Int, max: Int, value: Int) {
        kotlin.require(value >= min && value <= max) { String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, min, max) }
    }
}