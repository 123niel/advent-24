import com.toldoven.aoc.notebook.AocClient
import util.*
import kotlin.time.measureTimedValue

data class State(
    val guard: Point,
    val direction: Direction,
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

    val (initialGuard, obstructions) = lines.extractPositions()

    val initialState = State(initialGuard, Direction.Up)

    val boundaries = Boundaries(Point(0, 0), Point(lines.first().lastIndex, lines.lastIndex))

    val part1 =
        measureTimedValue {
            part1(initialState, obstructions, boundaries)
        }
    println("Part 1: ${part1.value.size} (${part1.duration})")
    measureTimedValue {
        part2(initialState, obstructions, part1.value, boundaries)
    }.also {
        println("Part 2: ${it.value} (${it.duration})")
    }
}

fun part1(
    initialState: State,
    obstructions: Set<Point>,
    boundaries: Boundaries,
): Set<Point> {
    var state = initialState
    val visited = mutableSetOf<Point>()
    var left = false

    while (!left) {
        val next = state.move(obstructions)
        if (!next.guard.inBounds(boundaries)) {
            left = true
        } else {
            visited.add(next.guard)
            state = next
        }
    }
    return visited
}

fun part2(
    initialState: State,
    obstructions: Set<Point>,
    route: Set<Point>,
    boundaries: Boundaries,
): Int =
    (route - initialState.guard)
        .map { obstructions + it }
        .map { modified ->
            var state = initialState
            var left = false
            var loop = false
            val visited: MutableSet<State> = mutableSetOf()

            while (!left && !loop) {
                val next = state.move(modified)

                if (visited.contains(next)) {
                    loop = true
                } else if (!next.guard.inBounds(boundaries)) {
                    left = true
                } else {
                    visited.add(next)
                    state = next
                }
            }

            loop
        }.count { it }

private fun State.move(obstructions: Set<Point>): State {
    val next = guard.step(direction)

    var nextDirection = direction

    while (next.step(nextDirection) in obstructions) {
        nextDirection = nextDirection.turnRight()
    }

    return State(next, nextDirection)
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
