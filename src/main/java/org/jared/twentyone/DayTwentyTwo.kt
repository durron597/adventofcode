package org.jared.twentyone

import org.jared.util.ScanIterator
import java.util.*
import kotlin.math.max
import kotlin.math.min

fun main() {
    runDay(true)
    runDay(false)
}

private fun runDay(isPartOne: Boolean) {
    val iter = ScanIterator(Scanner(DayOne::class.java.getResourceAsStream("/2021/day22_input.txt")!!))

    val finder = Regex("-?\\d+")

    var cubeList = listOf<Pair<Cube, Boolean>>()

    while (iter.hasNext()) {
        val next = iter.next()
        val type = next.startsWith("on")

        val result = finder.findAll(next).map { it.groupValues[0].toInt() }
        val cube = Cube(*(result.toList().toIntArray()))

        if (isPartOne) {
            if (cube.x1 > 50 || cube.x2 < -50 || cube.y1 > 50 || cube.y2 < -50 || cube.z1 > 50 || cube.z2 < -50) continue
        }

        cubeList = addCube(cubeList, Pair(cube, type))
    }

    val result = cubeList.fold(0L) { acc, p -> acc + p.spr(::volume) }
    println(result)
}

fun <A, B, R> Pair<A, B>.spr(f: (A, B) -> R) = f(first, second)

fun volume(cube: Cube, type: Boolean): Long =
         ((cube.x2 - cube.x1 + 1).toLong() *
                 (cube.y2 - cube.y1 + 1).toLong() *
                 (cube.z2 - cube.z1 + 1).toLong()) *
                 if (type) { 1L } else { -1L }

fun addCube(cubeList: List<Pair<Cube, Boolean>>, cubeToAdd: Pair<Cube, Boolean>):
        List<Pair<Cube, Boolean>>
{
    val updatedList = cubeList.flatMap { p -> intersectCube(p, cubeToAdd.first) }
    return if (cubeToAdd.second) {
        updatedList + cubeToAdd
    } else {
        updatedList
    }
}

fun intersectCube(cube1Pair: Pair<Cube, Boolean>, cube2: Cube): List<Pair<Cube, Boolean>> {
    val cube1 = cube1Pair.first
    val newCube = Cube(max(cube1.x1, cube2.x1), min(cube1.x2, cube2.x2),
            max(cube1.y1, cube2.y1), min(cube1.y2, cube2.y2),
            max(cube1.z1, cube2.z1), min(cube1.z2, cube2.z2))
    return if (validateCube(newCube)) {
        if (newCube == cube1) {
            listOf()
        } else {
            listOf(cube1Pair, Pair(newCube, !cube1Pair.second))
        }
    } else {
        listOf(cube1Pair)
    }
}

fun validateCube(newCube: Cube): Boolean =
    newCube.x1 <= newCube.x2 && newCube.y1 <= newCube.y2 && newCube.z1 <= newCube.z2

data class Cube (val x1: Int, val x2: Int, val y1: Int, val y2: Int, val z1: Int, val z2: Int) {
    constructor(vararg arr: Int): this(arr[0], arr[1], arr[2], arr[3], arr[4], arr[5])
}