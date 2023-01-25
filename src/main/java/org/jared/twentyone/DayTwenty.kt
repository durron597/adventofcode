package org.jared.twentyone

import org.jared.util.ScanIterator
import java.util.*

fun main(args: Array<String>) {
    val iter = ScanIterator(Scanner(DayOne::class.java.getResourceAsStream("/2021/day20_input.txt")!!))

    val template = iter.next().map { c -> c == '#' }.toBooleanArray()

    iter.next()

    val stringList = iter.toList()

    val grid = Array(stringList.size() + 104) { BooleanArray(stringList[0].length + 104) }

    for (y in 0 until stringList.size()) {
        for (x in 0 until stringList[0].length) {
            grid[y+52][x+52] = stringList[y][x] == '#'
        }
    }

    var nextGrid: Array<BooleanArray>
    var nextGrid2: Array<BooleanArray> = grid

    for (i in 0 until 25) {
         nextGrid = generateNextGrid(nextGrid2, template, false)
         nextGrid2 = generateNextGrid(nextGrid, template, true)
    }

    renderGrid(nextGrid2)

    val countTrue = nextGrid2.fold(0) { acc, r ->
        acc + r.fold(0) { acc2, b ->
            acc2 + if (b) {
                1
            } else {
                0
            }
        }
    }

    println(countTrue)
}

private fun generateNextGrid(grid: Array<BooleanArray>, template: BooleanArray, defaultValue: Boolean): Array<BooleanArray> {
    val nextGrid = Array(grid.size) { BooleanArray(grid[0].size) }

    for (y in grid.indices) {
        for (x in grid[0].indices) {
//            val defaultValue = if (grid[0][0]) {
//                template[template.size - 1]
//            } else {
//                template[0]
//            }
            val templateResult = computeNextValue(grid, template, x, y, defaultValue)
            nextGrid[y][x] = templateResult
        }
    }
    return nextGrid
}

private fun renderGrid(grid: Array<BooleanArray>) {
    for (row in grid) {
        for (cell in row) {
            print(if (cell) {
                '#'
            } else {
                '.'
            })
        }
        println()
    }
}

private fun computeNextValue(grid: Array<BooleanArray>, template: BooleanArray, x: Int, y: Int, defaultValue: Boolean): Boolean {
    val deltaPairs = (-1..1).flatMap { yD -> (-1..1).map { xD -> Pair(xD, yD) } }
            .map { (xD, yD) ->
                grid.elementAtOrElse(y + yD) { _ -> BooleanArray(0) }
                        .elementAtOrElse(x + xD) { _ -> defaultValue }
            }
    return lookup(template, deltaPairs)
}

fun lookup(template: BooleanArray, indices: List<Boolean>): Boolean {
    var total = 0
    for (bool in indices) {
        total = total shl 1
        total += if (bool) { 1 } else { 0}
    }
    return template[total]
}