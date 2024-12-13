package aoc

import aoc.util.println
import com.toldoven.aoc.notebook.AocClient

fun main() {
    val input = AocClient.fromEnv().interactiveDay(2024, 9).input()

    part1(input).println()
    part2(input).println()
}

private fun part1(input: String): Long {
    val disk = input.parse1()

    var finished = false

    while (!finished) {
        val emptyIndex = disk.indexOfFirst { it == null }
        val fileIndex = disk.indexOfLast { it != null }

        if (emptyIndex > fileIndex) {
            finished = true
        } else {
            disk.swap(emptyIndex, fileIndex)
        }
    }

    val part1 = disk.checksum()
    return part1
}

private fun List<Int?>.checksum() = mapIndexed { index, id ->
    if (id == null) 0 else index * id.toLong()
}.sum()

sealed interface Block {
    val size: Int
}

data class Empty(override val size: Int) : Block

data class File(
    val id: Int,
    override val size: Int,
) : Block

private fun part2(input: String): Long {
    val disk = input.parse2()

    val maxFileId = disk
        .mapNotNull {
            when (it) {
                is Empty -> null
                is File -> it.id
            }
        }.max()

    for (fileId in maxFileId downTo 0) {
        val fileIndex = disk.indexOfLast { it is File && it.id == fileId }
        val file = disk[fileIndex]

        val emptyIndex = disk.indexOfFirst { it is Empty && it.size >= file.size }
        if (emptyIndex == -1) {
            continue
        }
        val empty = disk[emptyIndex]

        if (fileIndex > emptyIndex) {
            val diff = empty.size - file.size

            disk[emptyIndex] = file
            disk[fileIndex] = Empty(empty.size - diff)

            if (diff > 0) {
                disk.add(emptyIndex + 1, Empty(diff))
            }
        }
    }

    return disk.spread().checksum()
}

private fun List<Block>.spread() = buildList {
    this@spread.forEach { block ->
        when (block) {
            is Empty -> for (i in 0 until block.size) {
                add(null)
            }

            is File -> for (i in 0 until block.size) {
                add(block.id)
            }
        }
    }
}

private fun <T> MutableList<T>.swap(emptyIndex: Int, fileIndex: Int) {
    val temp = this[emptyIndex]
    this[emptyIndex] = this[fileIndex]
    this[fileIndex] = temp
}

private fun String.parse1() = buildList {
    var file = true
    var fileIndex = 0

    this@parse1.forEach { char ->
        val count = char.toString().toInt()

        when (file) {
            true -> {
                val index = fileIndex++
                for (i in 0 until count) {
                    add(index)
                }
            }

            false -> for (i in 0 until count) {
                add(null)
            }
        }
        file = !file
    }
}.toMutableList()

private fun String.parse2() = buildList {
    var file = true
    var fileIndex = 0

    this@parse2.forEach { char ->
        val size = char.toString().toInt()

        when (file) {
            true -> add(File(fileIndex++, size))
            false -> add(Empty(size))
        }
        file = !file
    }
}.toMutableList()
