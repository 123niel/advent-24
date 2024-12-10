import com.toldoven.aoc.notebook.AocClient
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger


enum class Direction {
    Up, Right, Down, Left
}

fun Direction.turnRight() = when (this) {
    Direction.Up -> Direction.Right
    Direction.Right -> Direction.Down
    Direction.Down -> Direction.Left
    Direction.Left -> Direction.Up
}

data class Point(val x: Int, val y: Int)

fun Point.step(direction: Direction) = when (direction) {
    Direction.Up -> Point(x, y - 1)
    Direction.Right -> Point(x + 1, y)
    Direction.Down -> Point(x, y + 1)
    Direction.Left -> Point(x - 1, y)
}

fun Point.inBounds(topLeft: Point, bottomRight: Point) =
    x >= topLeft.x
            && x <= bottomRight.x
            && y >= topLeft.y
            && y <= bottomRight.y


data class State(val direction: Direction, val guard: Point, val visited: Set<Pair<Point, Direction>>)

fun List<String>.extractPositions(): Pair<Point, List<Point>> {

    val obstructions = mutableListOf<Point>()
    var guard: Point? = null

    forEachIndexed { y, line ->
        line.forEachIndexed { x, char ->
            when (char) {
                '^' -> guard = Point(x, y)
                '#' -> obstructions.add(Point(x, y))
            }
        }
    }

    return requireNotNull(guard) to obstructions.toList()
}


fun main() {

    val aoc = AocClient.fromEnv().interactiveDay(2024, 6)

    val lines = aoc.input().lines()

//    val lines = """
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
//    """.trimIndent().lines()

    val (initialGuard, obstructions) = lines.extractPositions()


    val initalState = State(Direction.Up, initialGuard, setOf(initialGuard to Direction.Up))

    val topLeft = Point(0, 0)
    val bottomRight = Point(lines.first().lastIndex, lines.lastIndex)

    val part1 = part1(initalState, obstructions, topLeft, bottomRight)

    println(part1)

    val part2 = part2(initalState, obstructions, topLeft, bottomRight)

    println(part2)
}

fun part1(
    initalState: State,
    obstructions: List<Point>,
    topLeft: Point,
    bottomRight: Point
): Int {
    val (_, _, visited) = move(initalState, obstructions, topLeft, bottomRight)
    return visited.map { it.first }.toSet().size
}

fun part2(
    initialState: State,
    obstructions: List<Point>,
    topLeft: Point,
    bottomRight: Point
): Int {

    val allMutations = sequence {
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
        val jobs = allMutations.asSequence().map { modifiedObstructions ->
            async {


                var state = initialState
                var left = false
                var loop = false

                while (!left && !loop) {

                    val next = state.tick(modifiedObstructions)


                    if (!next.guard.inBounds(topLeft, bottomRight)) left = true
                    else if (next.guard to next.direction in state.visited) loop = true
                    else state = next
                }

                loop
            }
        }

        val count = AtomicInteger(0)
        jobs.forEach {
            if (it.await()) {
                println(count.incrementAndGet())
            }
        }
        count.get()
    }
}

private fun move(
    initalState: State,
    obstructions: List<Point>,
    topLeft: Point,
    bottomRight: Point
) = generateSequence(initalState) {
    it.tick(obstructions)
}.takeWhile { (_, guard, _) ->
    guard.inBounds(topLeft, bottomRight)
}.last()

private fun State.tick(obstructions: List<Point>): State {

    val next = guard.step(direction)
    val willTurn = next.step(direction) in obstructions
    val nextDirection = if (willTurn) direction.turnRight() else direction

    return State(nextDirection, next, visited + (next to nextDirection))
}

fun State.printState(
    bottomRight: Point,
    obstructions: List<Point>
) {
    (0..bottomRight.y).forEach { y ->
        (0..bottomRight.x).forEach { x ->

            val point = Point(x, y)

            print(
                when (point) {
                    in obstructions -> "#"
                    in this.visited.map { it.first } -> "X"
                    this.guard -> when (this.direction) {
                        Direction.Up -> "^"
                        Direction.Right -> ">"
                        Direction.Down -> "v"
                        Direction.Left -> "<"
                    }

                    else -> "."
                }
            )

        }
        println()
    }
    println("\n")
}

