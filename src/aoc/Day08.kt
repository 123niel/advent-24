package aoc

import aoc.util.Boundaries
import aoc.util.Point
import aoc.util.boundaries
import aoc.util.inBounds
import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

fun <T> List<T>.uniqueCombinations(): List<Pair<T, T>> {
    val list = mutableListOf<Pair<T, T>>()

    val remaining = ArrayList(this)

    this.forEach { item ->
        remaining.forEach { other ->
            if (item != other) {
                list.add(Pair(item, other))
            }
        }

        remaining.remove(item)
    }

    return list.toList()
}

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 8)

    val lines = aoc.input().lines()

    val boundaries = lines.boundaries()

    val antennasMap = mutableMapOf<Char, MutableList<Point>>()

    lines.forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            if (c != '.') {
                antennasMap.getOrPut(c, ::mutableListOf).add(Point(x, y))
            }
        }
    }

    part1(antennasMap, boundaries).println()

    part2(antennasMap, boundaries).println()
}

private fun part1(antennasMap: Map<Char, List<Point>>, boundaries: Boundaries): Int {
    val antiNodes = mutableSetOf<Point>()

    antennasMap.forEach { (_, list) ->

        list.uniqueCombinations().forEach { (a, b) ->

            val diff = a - b
            antiNodes.add(a + diff)
            antiNodes.add(b - diff)
        }
    }

    val part1 = antiNodes.filter { it.inBounds(boundaries) }.size
    return part1
}

private fun part2(antennasMap: Map<Char, List<Point>>, boundaries: Boundaries): Int {
    val antiNodes = mutableSetOf<Point>()

    antennasMap.forEach { (_, list) ->

        list.uniqueCombinations().forEach { (a, b) ->

            val diff = a - b

            generateSequence(a) {
                it + diff
            }.takeWhile { it.inBounds(boundaries) }.forEach(antiNodes::add)

            generateSequence(b) {
                it - diff
            }.takeWhile { it.inBounds(boundaries) }.forEach(antiNodes::add)
        }
    }

    return antiNodes.size
}

operator fun Point.minus(other: Point) = Point(this.x - other.x, this.y - other.y)

operator fun Point.plus(other: Point) = Point(this.x + other.x, this.y + other.y)
