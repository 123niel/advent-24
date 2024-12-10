import com.toldoven.aoc.notebook.AocClient
import util.*
import kotlin.time.measureTimedValue

data class State(
    val direction: Direction,
    val guard: Point,
)

fun List<String>.extractPositions(): Pair<Point, Set<Point>> {
    val obstructions = mutableSetOf<Point>()
    var guard: Point? = null

    forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            when (char) {
                '^' -> guard = Point(x, y)
                '#' -> obstructions.add(Point(x, y))
            }
        }
    }

    return requireNotNull(guard) to obstructions.toSet()
}

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 6)

    val lines = aoc.input().lines()

//    val lines =
//        """
//        ....#.....
//        .........#
//        ..........
//        ..#.......
//        .......#..
//        ..........
//        .#..^.....
//        ........#.
//        #.........
//        ......#...
//        """.trimIndent().lines()

    val (initialGuard, obstructions) = lines.extractPositions()

    val initialState = State(Direction.Up, initialGuard)

    val boundaries = Boundaries(Point(0, 0), Point(lines.first().lastIndex, lines.lastIndex))

    measureTimedValue {
        part1(initialState, obstructions, boundaries)
    }.println()

    measureTimedValue {
        part2(initialState, obstructions, boundaries)
    }.println()
}

fun part1(
    initialState: State,
    obstructions: Set<Point>,
    boundaries: Boundaries,
): Int {
    var state = initialState
    val visited = mutableSetOf<Point>()
    var left = false

    while (!left) {
        val next = state.tick(obstructions)
        if (!next.guard.inBounds(boundaries)) {
            left = true
        } else {
            visited.add(next.guard)
            state = next
        }
    }
    return visited.size
}

fun part2(
    initialState: State,
    obstructions: Set<Point>,
    boundaries: Boundaries,
): Int =
    sequence {
        boundaries.loopArea { point ->
            if (point != initialState.guard && point !in obstructions) {
                yield(obstructions + point)
            }
        }
    }.map { modified ->
        var state = initialState
        var left = false
        var loop = false
        val visited = mutableSetOf<Pair<Point, Direction>>()

        while (!left && !loop) {
            val next = state.tick(modified)

            if (!next.guard.inBounds(boundaries)) {
                left = true
            } else if (next.guard to next.direction in visited) {
                loop = true
            } else {
                visited.add(next.guard to next.direction)
                state = next
            }
        }

        loop
    }.filter { it }
        .count()

private fun State.tick(obstructions: Set<Point>): State {
    val next = guard.step(direction)
    val willTurn = next.step(direction) in obstructions
    val nextDirection = if (willTurn) direction.turnRight() else direction

    return State(nextDirection, next)
}

fun State.printState(
    visited: Set<Point>,
    boundaries: Boundaries,
    obstructions: Set<Point>,
) {
    boundaries.loopArea(
        afterLine = { println() },
    ) { point ->
        print(
            when (point) {
                in obstructions -> "#"
                in visited -> "X"
                guard ->
                    when (direction) {
                        Direction.Up -> "^"
                        Direction.Right -> ">"
                        Direction.Down -> "v"
                        Direction.Left -> "<"
                    }

                else -> "."
            },
        )
    }
}
