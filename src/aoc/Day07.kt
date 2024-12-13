package aoc

import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

data class Equation(
    val testVal: Long,
    val numbers: List<Long>,
)

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 7)

    val equations =
        aoc.input().lines().map { line ->

            val testVal = line.substringBefore(": ").toLong()
            val numbers = line.substringAfter(": ").split(" ").map { it.trim().toLong() }

            Equation(testVal, numbers)
        }

    equations
        .filter { equ ->
            equ.numbers.possibleSolutions().any { res -> res == equ.testVal }
        }.sumOf { it.testVal }
        .println()

    equations
        .filter { equ ->
            equ.numbers.possibleSolutions(includeConcat = true).any { res -> res == equ.testVal }
        }.sumOf { it.testVal }
        .println()
}

private fun List<Long>.possibleSolutions(includeConcat: Boolean = false) = drop(1)
    .fold(listOf(first())) { acc, num ->
        buildList {
            acc.forEach {
                add(it + num)
                add(it * num)
                if (includeConcat) add("$it$num".toLong())
            }
        }
    }
