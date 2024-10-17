package com.slope.game.utils

data class TriContainer<A, B, C>(var first: A, var second: B, var third: C) {
    override fun toString(): String {
        return "TriContainer(first=$first, second=$second, third=$third)"
    }
}