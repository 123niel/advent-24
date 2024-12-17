package aoc.day15

import aoc.util.Direction
import aoc.util.Point
import aoc.util.forEach2DIndexed
import aoc.util.println
import aoc.util.step
import com.toldoven.aoc.notebook.AocClient

sealed interface Object {
    var pos: Point
}

data class Robot(override var pos: Point) : Object

data class Box(override var pos: Point) : Object

data class Wall(private val _pos: Point) : Object {
    override var pos: Point
        get() = _pos
        set(value) = error("Invalid")
}

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 15)
    val input = aoc.input()

    var (robot, warehouse, moves) = parseInput(input)

    val boxes = warehouse.filterValues { it is Box }.map { it.value as Box }
    val walls = warehouse.filterValues { it is Wall }.map { it.value as Wall }

    for (move in moves) {
        var next = robot.pos.step(move)
        var nextObject = getNextObject(boxes, next, walls)

        val queue = mutableListOf<Object>(robot)

        while (nextObject is Box) {
            queue.add(nextObject)
            next = next.step(move)
            nextObject = getNextObject(boxes, next, walls)
        }

        if (nextObject == null) {
            while (queue.isNotEmpty()) {
                queue.removeLast().apply {
                    pos = pos.step(move)
                }
            }
        }
    }

    boxes.sumOf { it.pos.y * 100 + it.pos.x }.println()
}

private fun getNextObject(
    boxes: List<Box>,
    next: Point,
    walls: List<Wall>,
) = boxes.firstOrNull { it.pos == next } ?: walls.firstOrNull { it.pos == next }

fun parseInput(input: String): Triple<Robot, Map<Point, Object>, List<Direction>> {
    val warehouseInput = input.substringBefore("\n\n")

    lateinit var robot: Point

    val warehouse = buildMap {
        warehouseInput.lines().forEach2DIndexed { point, char ->
            if (char == '#') {
                val u = put(point, Wall(point))
            } else if (char == 'O') {
                val u = put(point, Box(point))
            } else if (char == '@') {
                robot = point
            }

            Unit
        }
    }

    val moves = input
        .substringAfter("\n\n")
        .replace("\n", "")
        .map {
            when (it) {
                '<' -> Direction.Left
                '^' -> Direction.Up
                '>' -> Direction.Right
                'v' -> Direction.Down
                else -> null
            }
        }.filterNotNull()

    return Triple(Robot(robot), warehouse, moves)
}
