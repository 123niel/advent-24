package aoc

import aoc.util.Boundaries
import aoc.util.Point
import aoc.util.inBounds
import aoc.util.println
import com.toldoven.aoc.notebook.AocClient
import kotlin.time.measureTimedValue

data class Robot(
    val pos: Point,
    val vel: Point,
)

fun Robot.move(): Robot {
    var x = pos.x + vel.x
    var y = pos.y + vel.y

    if (x >= width) x -= width
    if (x < 0) x += width
    if (y >= height) y -= height
    if (y < 0) y += height

    return copy(pos = Point(x, y))
}

fun List<Robot>.moveAll() = map { it.move() }

const val width = 101
const val height = 103

private fun <E> Collection<E>.hasDuplicates() = this.size != this.distinct().size

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 14)
    val input = aoc.input()

    var robots = input.lines().map { line ->
        val (px, py, vx, vy) = Regex("p=(-?\\d+),(-?\\d+) v=(-?\\d+),(-?\\d+)").matchEntire(line)!!.destructured
        Robot(Point(px.toInt(), py.toInt()), Point(vx.toInt(), vy.toInt()))
    }

    measureTimedValue {
        for (i in 0 until 100) {
            robots = robots.moveAll()
        }

        val middleX = width / 2
        val middleY = height / 2

        listOf(
            Boundaries(Point(0, 0), Point(middleX - 1, middleY - 1)),
            Boundaries(Point(middleX + 1, 0), Point(width - 1, middleY - 1)),
            Boundaries(Point(0, middleY + 1), Point(middleX - 1, height - 1)),
            Boundaries(Point(middleX + 1, middleY + 1), Point(width - 1, height - 1)),
        ).map { bounds -> robots.count { it.pos.inBounds(bounds) } }.fold(1) { a, b -> a * b }
    }.println()

    measureTimedValue {
        for (i in 100..100000000000) {
            if (!robots.map { it.pos }.hasDuplicates()) {
                return@measureTimedValue i
            }
            robots = robots.moveAll()
        }
    }.println()

    robots.render().println()
}

fun List<Robot>.render() = buildString {
    for (y in 0 until height) {
        for (x in 0 until width) {
            append(if (this@render.any { it.pos == Point(x, y) }) '#' else '.')
        }
        append(("\n"))
    }
    println()
}
