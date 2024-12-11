package util

enum class Direction {
    Up,
    Right,
    Down,
    Left,
}

fun Direction.turnRight() =
    when (this) {
        Direction.Up -> Direction.Right
        Direction.Right -> Direction.Down
        Direction.Down -> Direction.Left
        Direction.Left -> Direction.Up
    }

data class Point(
    val x: Int,
    val y: Int,
)

operator fun List<String>.get(point: Point) = this[point.y][point.x]
operator fun List<String>.get(x: Int, y: Int) = this[y][x]

fun Point.step(direction: Direction) =
    when (direction) {
        Direction.Up -> Point(x, y - 1)
        Direction.Right -> Point(x + 1, y)
        Direction.Down -> Point(x, y + 1)
        Direction.Left -> Point(x - 1, y)
    }

data class Boundaries(
    val topLeft: Point,
    val bottomRight: Point,
) {
    val xRange = (topLeft.x..bottomRight.x)
    val yRange = (topLeft.y..bottomRight.y)
}

fun Point.inBounds(boundaries: Boundaries) = x in boundaries.xRange && y in boundaries.yRange

inline fun Boundaries.loopArea(
    afterLine: () -> Unit = {},
    action: (Point) -> Unit,
) = yRange.forEach { y ->
    xRange.forEach { x ->
        action(Point(x, y))
    }
    afterLine()
}
