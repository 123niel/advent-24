package aoc

import aoc.util.Vector
import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

fun buttonOf(str: String): Vector {
    val (xs, ys) = str.drop(10).split(", ")

    return Vector(xs.drop(1).toDouble(), ys.drop(1).toDouble())
}

fun priceOf(str: String): Vector {
    val (xs, ys) = str.drop(7).split(", ")
    return Vector(xs.drop(2).toDouble(), ys.drop(2).toDouble())
}

data class Machine(
    val buttonA: Vector,
    val buttonB: Vector,
    val price: Vector,
)

private fun Machine.minimalTokens(): Long? {
    val b = (price.y * buttonA.x - price.x * buttonA.y) / (buttonB.y * buttonA.x - buttonB.x * buttonA.y)
    val a = (price.x - b * buttonB.x) / buttonA.x

    return if (a % 1 == 0.0 && b % 1 == 0.0) {
        a.toLong() * 3 + b.toLong()
    } else {
        null
    }
}

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 13)
    val input = aoc.input()

    val machines = input.split("\n\n").map { chunk ->
        val lines = chunk.lines()
        Machine(buttonOf(lines[0]), buttonOf(lines[1]), priceOf(lines[2]))
    }

    machines.sumOf { machine -> machine.minimalTokens() ?: 0 }.println()

    machines
        .sumOf { machine ->
            machine
                .copy(
                    price = Vector(
                        x = machine.price.x + 10000000000000,
                        y = machine.price.y + 10000000000000,
                    ),
                ).minimalTokens() ?: 0
        }.println()
}
