import com.toldoven.aoc.notebook.AocClient
import util.Point
import util.get
import util.println

fun main() {
    val aoc = AocClient.fromEnv().interactiveDay(2024, 4)


    val lines = aoc.input().lines()


    part1(lines).println()
    part2(lines).println()
}


private fun part1(lines: List<String>): Int {


    var counter = 0

    lines.indices.forEach { y ->
        lines.first().indices.forEach { x ->

            val point = Point(x,y)
            counter += listOf(
                lines.lookRight(point),
                lines.lookLeft(point),
                lines.lookDown(point),
                lines.lookUp(point),
                lines.lookLeftUp(point),
                lines.lookRightUp(point),
                lines.lookLeftDown(point),
                lines.lookRightDown(point),

            ).count { it.startsWith("XMAS") }
        }
    }

    return counter
}

private fun part2(lines: List<String>): Int {
    var counter = 0

    lines.indices.forEach { y ->
        lines.first().indices.forEach { x ->
            val point = Point(x,y)

            val here = lines[point]

            if(here == 'A') {
                val topLeft = lines.lookLeftUp(point).getOrElse(1) {' '}
                val topRight = lines.lookRightUp(point).getOrElse(1) {' '}
                val bottomLeft = lines.lookLeftDown(point).getOrElse(1) {' '}
                val bottomRight = lines.lookRightDown(point).getOrElse(1) {' '}

                val tlToBr = "$topLeft$here$bottomRight"
                val trToBl = "$topRight$here$bottomLeft"

                if((tlToBr == "MAS" || tlToBr =="SAM") && (trToBl == "MAS" || trToBl =="SAM")) {
                    counter++
                }
            }
            lines.lookLeftUp(point).startsWith("M") && lines.lookRightDown(point).startsWith("S")
        }
    }

    return counter
}


private fun List<String>.lookRight(start: Point) = this[start.y].substring(start.x)
private fun List<String>.lookLeft(start: Point) = this[start.y].substring(0.. start.x).reversed()
private fun List<String>.lookDown(start:Point) = this.drop(start.y).map { it[start.x] }.joinToString("")
private fun List<String>.lookUp(start:Point) = this.map { it[start.x] }.joinToString("").substring(0..start.y).reversed()
private fun List<String>.lookLeftUp(start: Point) = buildString {
    var x = start.x
    var y = start.y

    while (y >= 0 &&  x >= 0) {
        append(this@lookLeftUp[x,y])
        x--
        y--
    }
}
private fun List<String>.lookLeftDown(start: Point) = buildString {
    var x = start.x
    var y = start.y

    while (y <= this@lookLeftDown.lastIndex &&  x >= 0) {
        append(this@lookLeftDown[x,y])
        x--
        y++
    }
}
private fun List<String>.lookRightUp(start: Point) = buildString {
    var x = start.x
    var y = start.y

    while (y >= 0 &&  x <= this@lookRightUp.first().lastIndex) {
        append(this@lookRightUp[x,y])
        x++
        y--
    }
}
private fun List<String>.lookRightDown(start: Point) = buildString {
    var x = start.x
    var y = start.y

    while (y <= this@lookRightDown.lastIndex &&  x <= this@lookRightDown.first().lastIndex) {
        append(this@lookRightDown[x,y])
        x++
        y++
    }
}