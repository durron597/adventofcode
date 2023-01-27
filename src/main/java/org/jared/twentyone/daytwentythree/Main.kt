package org.jared.twentyone.daytwentythree

/**
Day One
#############
#89.C.D.E.BA#
###1#3#5#7###
  #0#2#4#6#
  #########

#############
#...........#
###B#A#B#C###
  #D#A#D#C#
  #########

Day Two (positioned numbers in base 36 for single character names). '.' spots are illegal
#############
#GH.K.L.M.IJ#
###3#7#B#F###
  #2#6#A#E#
  #1#5#9#D#
  #0#4#8#C#
  #########

#############
#...........#
###B#A#B#C###
  #D#C#B#A#
  #D#B#A#C#
  #D#A#D#C#
  #########
 */
fun main() {
    val example = runDayOne(listOf(1, 2, 4, 3, 3, 2, 1, 4, 0, 0, 0, 0, 0, 0, 0))
    printResult(example)

    val dayOne = runDayOne(listOf(4, 2, 1, 1, 4, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0))
    printResult(dayOne)

    val dayTwo = runDayTwo()
    printResult(dayTwo)
}

private fun printResult(result: ND?) {
    if (result != null) Renderer().printRecurse(result)
    println(result)
    println("-----------------------")
}

private fun runDayOne(start: List<Int>): ND? {
    val pb = PathBuilder()
    val allPaths: Map<Pair<Int, Int>, Path> = pb.allPathsDayOne()
    val end = listOf(1, 1, 2, 2, 3, 3, 4, 4, 0, 0, 0, 0, 0, 0, 0)
    return DijkstraRunner(allPaths, start, end).run()
}

private fun runDayTwo(): ND? {
    val pb = PathBuilder()
    val start = listOf(4, 4, 4, 2, 1, 2, 3, 1, 4, 1, 2, 2, 3, 3, 1, 3, 0, 0, 0, 0, 0, 0, 0)
    val end = listOf(1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 0, 0, 0, 0, 0, 0, 0)

    val allPaths1 = pb.allPathsDayTwo()

    return DijkstraRunner(allPaths1, start, end).run()
}