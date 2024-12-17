package aoc

import aoc.util.Point
import aoc.util.get
import aoc.util.neighbours
import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

fun main() {

    val aoc = AocClient.fromEnv().interactiveDay(2024, 12)
    val input = aoc.input()

    val regions = input.lines().toMap()

    regions.values.distinct().sumOf { regionId ->
        val points = regions.filter { it.value == regionId }.keys

        val area = points.size

        val perimeter = points.calculatePerimeter()

        area * perimeter
    }.println()

}

private fun Set<Point>.calculatePerimeter(): Int = sumOf { point ->
    point.neighbours()
        .filter { it !in this@calculatePerimeter }
        .size
}

private fun List<String>.toMap(): MutableMap<Point, Int> {
    val regionMapping = mutableMapOf<Point, Int>()
    var regionId = -1
    forEachIndexed { y, line ->
        line.forEachIndexed { x, c ->
            val point = Point(x, y)

            if (point !in regionMapping) {
                regionId++
                val toVisit = mutableListOf(point)

                while (toVisit.isNotEmpty()) {
                    val current = toVisit.removeFirst()
                    regionMapping[current] = regionId
                    toVisit += current.neighbours()
                        .filter { this@toMap[it] == this@toMap[current] && it !in toVisit && it !in regionMapping }
                }

            }

        }
    }
    return regionMapping
}
