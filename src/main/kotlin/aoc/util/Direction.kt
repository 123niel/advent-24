package aoc.util

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
