package org.jared.twentyone.daytwentythree

class PathBuilder {
    fun allPathsDayOne(): Map<Pair<Int, Int>, Path> {
        return allPaths(seedPathsDayOne()).filterKeys { allowedPairs(2).contains(it) }
    }

    fun allPathsDayTwo(): Map<Pair<Int, Int>, Path> {
        return allPaths(seedPathsDayTwo()).filterKeys { allowedPairs(4).contains(it) }
    }
    fun allPaths(initialPaths: Map<Pair<Int, Int>, Path>): Map<Pair<Int, Int>, Path> =
            initialPaths.keys.flatMap { p -> p.toList() }
                    .toSet()
                    .map { bfs(it, initialPaths) }.reduce { m1, m2 -> m1 + m2 }

    private fun bfs(id: Int, initialPaths: Map<Pair<Int, Int>, Path>): Map<Pair<Int, Int>, Path> {
        val target = mutableMapOf<Pair<Int, Int>, Path>()
        val queue = initialPaths.filterKeys { (s, _) -> s == id }.entries.map { (k, v) -> Pair(k, v) }.toMutableList()

        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            if (target.containsKey(next.first)) continue
            target[next.first] = next.second
            val result = initialPaths.filterKeys { (s, _) -> s == next.first.second }
                    .map { (k, v) ->
                        Pair(Pair(next.first.first, k.second),
                                Path(next.first.first, k.second, next.second.cost + v.cost, next.second.between + k.first))
                    }
            result.forEach(queue::add)
        }

        return target.toMap()
    }

    private fun allowedPairs(roomSize: Int): List<Pair<Int, Int>> {
        val roomToRoom = (0..roomSize * 3 step roomSize).flatMap { i ->
            (0..roomSize * 3 step roomSize)
                    .map { Pair(i, it) }
        }
                .filter { (i, j) -> i != j }
                .flatMap { (i, j) -> (0 until roomSize).map { Pair(i + it, j) } }
                .flatMap { (i, j) -> (0 until roomSize).map { Pair(i, j + it) } }
        val roomToHallway = (0..roomSize * 3 step roomSize)
                .flatMap { i -> (roomSize * 4..roomSize * 4 + 6).map { Pair(i, it) } }
                .flatMap { (i, j) -> (0 until roomSize).map { Pair(i + it, j) } }
                .flatMap { (i, j) -> sequenceOf(Pair(i, j), Pair(j, i)) }
        return roomToRoom + roomToHallway
    }

    private fun seedPathsDayOne(): Map<Pair<Int, Int>, Path> {
        val oneCosts = (0..10 step 2).flatMap { listOf(Pair(it, it + 1), Pair(it + 1, it)) }
                .map { Path(it.first, it.second, 1, setOf()) }
        val twoCosts = listOf(
                Path(9, 12, 2, setOf()),
                Path(1, 9, 2, setOf()),
                Path(1, 12, 2, setOf()),
                Path(3, 12, 2, setOf()),
                Path(3, 13, 2, setOf()),
                Path(5, 13, 2, setOf()),
                Path(5, 14, 2, setOf()),
                Path(7, 14, 2, setOf()),
                Path(7, 11, 2, setOf()),
                Path(11, 14, 2, setOf()),
                Path(12, 13, 2, setOf()),
                Path(13, 14, 2, setOf()))
                .flatMap { p -> listOf(p, Path(p.target, p.source, 2, setOf())) }
        return (oneCosts + twoCosts).associateBy { p -> Pair(p.source, p.target) }
    }

    private fun seedPathsDayTwo(): Map<Pair<Int, Int>, Path> {
        val holeUps = (0..12 step 4).flatMap { it..it + 2 }
                .map { Path(it, it + 1, 1, setOf()) }
        val holeDowns = (0..12 step 4).flatMap { it + 1..it + 3 }
                .map { Path(it, it - 1, 1, setOf()) }
        val edges = (16..18 step 2).flatMap { listOf(Pair(it, it + 1), Pair(it + 1, it)) }
                .map { Path(it.first, it.second, 1, setOf()) }
        val twoCosts = listOf(
                Path(17, 20, 2, setOf()),
                Path(3, 17, 2, setOf()),
                Path(3, 20, 2, setOf()),
                Path(7, 20, 2, setOf()),
                Path(7, 21, 2, setOf()),
                Path(11, 21, 2, setOf()),
                Path(11, 22, 2, setOf()),
                Path(15, 22, 2, setOf()),
                Path(15, 19, 2, setOf()),
                Path(19, 22, 2, setOf()),
                Path(20, 21, 2, setOf()),
                Path(21, 22, 2, setOf()))
                .flatMap { p -> listOf(p, Path(p.target, p.source, 2, setOf())) }
        return (holeUps + holeDowns + edges + twoCosts).associateBy { p -> Pair(p.source, p.target) }
    }
}