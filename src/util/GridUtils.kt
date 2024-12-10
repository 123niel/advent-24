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
)

fun Point.inBounds(boundaries: Boundaries) =
    x >= boundaries.topLeft.x &&
        y >= boundaries.topLeft.y &&
        x <= boundaries.bottomRight.x &&
        y <= boundaries.bottomRight.y

inline fun Boundaries.loopArea(
    afterLine: () -> Unit = {},
    action: (Point) -> Unit,
) = (topLeft.y..bottomRight.y).forEach { y ->
    (topLeft.x..bottomRight.x).forEach { x ->
        action(Point(x, y))
    }
    afterLine()
}