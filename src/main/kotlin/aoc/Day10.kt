package aoc

import aoc.util.Point
import aoc.util.neighbours
import aoc.util.println
import com.toldoven.aoc.notebook.AocClient
import java.util.ArrayDeque
import java.util.Queue

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 10)

    val input = aoc.input().lines()

    val grid = parseInput(input)

    val trailHeads = grid.filter { it.value == 0 }.map { it.key }

    trailHeads.sumOf { calculateScore(it, grid) }.println()
    trailHeads.sumOf { calculateScore(it, grid, distinctPaths = true) }.println()
}

private fun calculateScore(
    head: Point,
    grid: Map<Point, Int>,
    distinctPaths: Boolean = false,
): Int {
    val queue: Queue<Point> = ArrayDeque<Point>().apply { add(head) }

    val nines: MutableCollection<Point> = if (distinctPaths) {
        mutableListOf()
    } else {
        mutableSetOf()
    }

    while (queue.isNotEmpty()) {
        val point = queue.poll()

        val value = grid[point]!!
        if (value == 9) {
            nines.add(point)
        } else {
            val nextPoints = point.neighbours().filter { grid[it] == value + 1 }
            queue.addAll(nextPoints)
        }
    }
    return nines.size
}

private fun parseInput(input: List<String>) = buildMap {
    input.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            put(Point(x, y), c.toString().toInt())
        }
    }
}
