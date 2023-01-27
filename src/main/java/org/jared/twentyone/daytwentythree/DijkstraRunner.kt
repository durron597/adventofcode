package org.jared.twentyone.daytwentythree

import java.util.PriorityQueue

class DijkstraRunner (private val allPaths: Map<Pair<Int, Int>, Path>,
                      private val start: List<Int>,
                      private val end: List<Int>){
    companion object {
        val AMPH_COST = listOf(1, 10, 100, 1000)
    }

    // This is a field to allow Dijkstra to remove from the neighbor list
    private val visited = mutableMapOf<List<Int>, ND>()

    // These are used everywhere but never change, though they do depend on room list.
    // Therefore, cache them and improve code readability
    private val maxRoom = start.size - 8
    private val roomSize = (maxRoom + 1) / 4
    private val colMax = (roomSize - 1) xor -1

    fun run(): ND? {
        val priorityQueue = PriorityQueue<ND> (compareBy{ it.d })

        val element = ND(start, 0, null)
        priorityQueue.add(element)
        visited[start] = element

        while (!priorityQueue.isEmpty()) {
            val next = priorityQueue.poll()

            if (next.node == end) return next

            val neighbors: List<ND> = computeNeighbors(next)
            neighbors.forEach { visited[it.node] = it }
            neighbors.forEach { priorityQueue.add(it) }
        }

        return null
    }

    private fun computeNeighbors(next: ND): List<ND> = (start.indices).filter { next.node[it] > 0 }
            .flatMap { computeNeighborForIndex(next, it) }

    private fun computeNeighborForIndex(current: ND, i: Int): Sequence<ND> {
        val allAllowedNeighbors = start.indices.asSequence().filter { it != i }
                .mapNotNull { allPaths[Pair(i, it)] }
                .filter(ensurePathIsClear(current))
                // allPaths doesn't contain hallway to hallway, so moving to hallway is always ok
                .filter { it.target > maxRoom || verifyRoom(current, it) }
        val dontUnnecessarilyStopInHallway = allAllowedNeighbors.filter {
            it.target <= maxRoom ||
                    (allAllowedNeighbors.filter { i -> i.target <= maxRoom }).toList().isEmpty()
        }
        // transform allowed Paths into ND, and ensure we want to visit this node (Dijkstra)
        return dontUnnecessarilyStopInHallway
                .map {
                    ND(buildUpdatedList(current.node, i, it.target),
                            computeCostByAmphipod(current, it, i), current)
                }
                .filter { it.d < (visited[it.node]?.d ?: Int.MAX_VALUE) }
    }

    private fun ensurePathIsClear(current: ND): (Path) -> Boolean =
            { current.node[it.target] == 0 &&
                    it.between.fold(0) { acc, j -> acc + current.node[j] } == 0 }

    private fun computeCostByAmphipod(current: ND, it: Path, i: Int) =
            current.d + (it.cost * (AMPH_COST[current.node[i] - 1]))

    private fun verifyRoom(current: ND, p: Path): Boolean {
        val column = p.target and colMax
        // don't move within column
        if (p.source and colMax == column) return false
        // confirm that moving into the correct column
        if ((column / roomSize) + 1 != current.node[p.source]) return false
        // confirm column can receive because bottom cell is either empty or the right type
        if (current.node[column] > 0 && current.node[column] != current.node[p.source]) return false
        // always move into the lowest empty spot
        val lowestEmpty = (column..column + (roomSize - 1)).map { col -> Pair(col, current.node[col]) }
                .filter { (_, v) -> v == 0 }
                .minBy { (idx, _) -> idx }
        if (lowestEmpty.first != p.target) return false
        return true
    }

    private fun buildUpdatedList(oldState: List<Int>, source: Int, target: Int): List<Int> {
        val list = oldState.toMutableList()
        list[target] = list[source]
        list[source] = 0
        return list.toList()
    }
}
