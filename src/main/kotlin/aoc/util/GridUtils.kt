package aoc.util

operator fun List<String>.get(point: Point) = this[point.y][point.x]

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