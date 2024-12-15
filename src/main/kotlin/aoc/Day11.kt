package aoc

import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

fun main() {

    val aoc = AocClient.fromEnv().interactiveDay(2024, 11)
    val input = aoc.input()

    val stones = input.split(" ").map { it.toLong() }

    stones.sumOf { calculateCount(it, 25) }.println()
    stones.sumOf { calculateCount(it, 75) }.println()
}

val cache = mutableMapOf<Pair<Long, Int>, Long>()

fun Long.blink(): List<Long> {
    val str = toString()
    return if (this == 0L) listOf(1)
    else if (str.length % 2 == 0) {
        listOf(
            str.substring(0, str.length / 2).toLong(),
            str.substring(str.length / 2).toLong()
        )
    } else listOf(this * 2024)
}

fun calculateCount(stone: Long, iterations: Int): Long {
    if (iterations == 0) return 1
    return cache.getOrPut(stone to iterations) {
        stone.blink().sumOf { calculateCount(it, iterations - 1) }
    }
}

