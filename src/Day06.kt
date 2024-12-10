import com.toldoven.aoc.notebook.AocClient
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.measureTimedValue

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

fun Point.inBounds(
    topLeft: Point,
    bottomRight: Point,
) = x >= topLeft.x &&
    x <= bottomRight.x &&
    y >= topLeft.y &&
    y <= bottomRight.y

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

    val initalState = State(Direction.Up, initialGuard)

    val topLeft = Point(0, 0)
    val bottomRight = Point(lines.first().lastIndex, lines.lastIndex)

    val part1 = part1(initalState, obstructions, topLeft, bottomRight)

    println(part1)

    val part2 =
        measureTimedValue {
            part2(initalState, obstructions, topLeft, bottomRight)
        }

    println(part2)
}

fun part1(
    initalState: State,
    obstructions: Set<Point>,
    topLeft: Point,
    bottomRight: Point,
): Int {
    val visited = move(initalState, obstructions, topLeft, bottomRight)
    return visited.size
}

fun part2(
    initialState: State,
    obstructions: Set<Point>,
    topLeft: Point,
    bottomRight: Point,
): Int {
    val allMutations =
        sequence {
            (0..bottomRight.y).forEach { y ->
                (0..bottomRight.x).forEach { x ->
                    when (Point(x, y)) {
                        initialState.guard -> {}
                        in obstructions -> {}
                        else -> {
                            yield(obstructions + Point(x, y))
                        }
                    }
                }
            }
        }.toList()

    return runBlocking {
        val jobs =
            allMutations.asFlow().transform { modifiedObstructions ->
                emit(
                    async {
                        var state = initialState
                        var left = false
                        var loop = false
                        val visited = mutableSetOf<Pair<Point, Direction>>()

                        while (!left && !loop) {
                            val next = state.tick(modifiedObstructions)

                            if (!next.guard.inBounds(topLeft, bottomRight)) {
                                left = true
                            } else if (next.guard to next.direction in visited) {
                                loop = true
//                                state.printState(visited.map { it.first }.toSet(), bottomRight, modifiedObstructions)
                            } else {
                                visited.add(next.guard to next.direction)
                                state = next
                            }
                        }

                        loop
                    },
                )
            }

        val count = AtomicInteger(0)
        jobs.collect {
            if (it.await()) {
                count.incrementAndGet()
            }
        }
        count.get()
    }
}

private fun move(
    initalState: State,
    obstructions: Set<Point>,
    topLeft: Point,
    bottomRight: Point,
): Set<Point> {
    var state = initalState
    val vistied = mutableSetOf<Point>()
    var left = false

    while (!left) {
        val next = state.tick(obstructions)
        if (!next.guard.inBounds(topLeft, bottomRight)) {
            left = true
        } else {
            state = next
            vistied.add(state.guard)
        }
    }

    return vistied.toSet()
}

private fun State.tick(obstructions: Set<Point>): State {
    val next = guard.step(direction)
    val willTurn = next.step(direction) in obstructions
    val nextDirection = if (willTurn) direction.turnRight() else direction

    return State(nextDirection, next)
}

fun State.printState(
    visited: Set<Point>,
    bottomRight: Point,
    obstructions: Set<Point>,
) {
    (0..bottomRight.y).forEach { y ->
        (0..bottomRight.x).forEach { x ->

            val point = Point(x, y)

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
        println()
    }
    println("\n")
}
