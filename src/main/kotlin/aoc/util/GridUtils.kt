package aoc.util

operator fun List<String>.get(point: Point) = this.getOrNull(point.y)?.getOrNull(point.x)

operator fun List<String>.get(x: Int, y: Int) = this[y][x]

data class Boundaries(
    val topLeft: Point,
    val bottomRight: Point,
) {
    val xRange = (topLeft.x..bottomRight.x)
    val yRange = (topLeft.y..bottomRight.y)
}

fun List<String>.boundaries() = Boundaries(
    Point(0, 0),
    Point(this.first().lastIndex, this.lastIndex),
)

fun Point.neighbours() = listOf(
    step(Direction.Up),
    step(Direction.Down),
    step(Direction.Left),
    step(Direction.Right),
)

inline fun List<String>.forEach2DIndexed(action: (point: Point, char: Char) -> Unit): Unit = forEachIndexed { y, line ->
    line.forEachIndexed { x, c -> action(Point(x, y), c) }
}
