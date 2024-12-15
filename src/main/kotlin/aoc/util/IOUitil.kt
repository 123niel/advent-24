package aoc.util

fun <T : Any?> T.println() = this.also { println(it) }
