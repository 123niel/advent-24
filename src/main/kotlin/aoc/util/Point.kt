package aoc.util

data class Point(
    val x: Int,
    val y: Int,
)

fun Point.step(direction: Direction) =
    when (direction) {
        Direction.Up -> Point(x, y - 1)
        Direction.Right -> Point(x + 1, y)
        Direction.Down -> Point(x, y + 1)
        Direction.Left -> Point(x - 1, y)
    }

fun Point.inBounds(boundaries: Boundaries) = x in boundaries.xRange && y in boundaries.yRange
