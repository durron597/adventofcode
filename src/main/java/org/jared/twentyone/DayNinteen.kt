package org.jared.twentyone

import org.jared.util.ScanIterator
import java.util.Scanner
import java.util.SortedMap
import kotlin.Comparator
import kotlin.math.abs

data class TransformRule(val offset: Int, val sign: Int, val source: Int, val target: Int)

fun main() {
    val iter = ScanIterator(Scanner(DayOne::class.java.getResourceAsStream("/2021/day19_input.txt")!!))

    val idToPoints = buildPointList(iter)

    val distances = idToPoints.mapValues { (_, v) -> toDistances(v) }
    val byDistance = pointPairsByDistance(distances)
    val elevenses = pairsWithElevenMatches(byDistance)
    val groupByScannerPairs = groupMatchesByScannerPairs(idToPoints, elevenses)
    val asInstructions = groupByScannerPairs.flatMap(::toInstructions).toMap()
    val fullInstructions = addAllMissingTransforms(asInstructions)
    val mergePoints = doMerge(idToPoints, fullInstructions)

    println("Part 1 answer: " + mergePoints.size)
    println("Part 2 answer: " + fullInstructions
            .mapValues { (_, v) -> v.fold(0) { acc, r -> acc + abs(r.offset) } }
            .maxBy { (_, v) -> v })
}

private fun buildPointList(iter: ScanIterator): Map<Int, List<Triple<Int, Int, Int>>> {
    var currentId = -1
    val idToPoints = mutableMapOf<Int, List<Triple<Int, Int, Int>>>()

    while (iter.hasNext()) {
        val nextStr = iter.next()

        if ("" == nextStr) {
            currentId = -1
            continue
        }
        if (nextStr[1] == '-') {
            currentId = nextStr.filter(Char::isDigit).toInt()
            continue
        }
        val points = stringToTriple(nextStr)
        idToPoints.merge(currentId, listOf(points)) { v1, v2 -> v1 + v2 }
    }
    return idToPoints.mapValues { (_, v) -> v.toList() }.toMap()
}

private fun stringToTriple(nextStr: String) = nextStr.split(",")
        .map(String::toInt)
        .windowed(3, 3)
        .map { Triple(it[0], it[1], it[2]) }
        .first()

private fun toDistances(pointList: List<Triple<Int, Int, Int>>): Map<Int, List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>> {
    return pointList.flatMap { p -> pointList.map { p2 -> Pair(p, p2) } }
            .filter { pair -> compareTo(pair.first, pair.second) < 0 }
            .map { (t1, t2) ->
                Triple((t2.first - t1.first) * (t2.first - t1.first) +
                        (t2.second - t1.second) * (t2.second - t1.second) +
                        (t2.third - t1.third) * (t2.third - t1.third), t1, t2)
            }
            .groupBy { (d, _, _) -> d }
}

private fun compareTo(first: Triple<Int, Int, Int>, second: Triple<Int, Int, Int>): Int {
    return Comparator.comparing { t: Triple<Int, Int, Int> -> t.first }
            .thenComparing { t: Triple<Int, Int, Int> -> t.second }
            .thenComparing { t: Triple<Int, Int, Int> -> t.third }
            .compare(first, second)
}

private fun pointPairsByDistance(distances: Map<Int, Map<Int, List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>>>):
        SortedMap<Int, List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>> =
        distances.entries.fold(mutableMapOf<Int, List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>>()
        ) { acc, e ->
            val allToMerge = e.value.mapValues { (_, v) -> v.map { (_, k2, k3) -> Triple(e.key, k2, k3) } }
            allToMerge.forEach { (k, v) -> acc.merge(k, v) { v1, v2 -> v1 + v2 } }
            acc
        }.toSortedMap()

private fun pairsWithElevenMatches(byDistance: SortedMap<Int, List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>>) =
        byDistance.mapValues { (_, v) -> findCandidatePairs(v) }
                .values
                .flatten()
                .groupBy { p -> p }
                .mapValues { (_, v) -> v.size }
                .filterValues { i -> i > 10 }

private fun findCandidatePairs(distanceList: List<Triple<Int, Triple<Int, Int, Int>, Triple<Int, Int, Int>>>):
        List<Pair<Pair<Int, Triple<Int, Int, Int>>, Pair<Int, Triple<Int, Int, Int>>>> {
    val uncrossed = distanceList.flatMap { (i, t1, t2) -> listOf(Pair(i, t1), Pair(i, t2)) }
    return uncrossed.flatMap { (s, t) -> uncrossed.map { (s2, t2) -> Pair(Pair(s, t), Pair(s2, t2)) } }
            .filter { (p1, p2) -> p1.first < p2.first }
}

private fun groupMatchesByScannerPairs(idToPoints: Map<Int, List<Triple<Int, Int, Int>>>, elevenses: Map<Pair<Pair<Int, Triple<Int, Int, Int>>, Pair<Int, Triple<Int, Int, Int>>>, Int>) =
        idToPoints.keys.toList()
                .flatMap { i -> idToPoints.keys.toList().map { j -> Pair(i, j) } }
                .filter { (i, j) -> i < j }
                .associate { (i, j) -> Pair(i, j) to elevenses.keys.filter { (p, p2) -> p.first == i && p2.first == j } }
                .filter { (_, v) -> v.size > 1 }

private fun toInstructions(entry: Map.Entry<Pair<Int, Int>, List<Pair<Pair<Int, Triple<Int, Int, Int>>, Pair<Int, Triple<Int, Int, Int>>>>>):
        List<Pair<Pair<Int, Int>, List<TransformRule>>> {
    val lToRinstList = mutableListOf<TransformRule>()
    val rToLinstList = mutableListOf<TransformRule>()

    val firstPair = entry.value[0]
    val secondPair = entry.value[1]

    for (firstIndex in 0..2) {
        for (secondIndex in 0..2) {
            for (sign in -1..1 step 2) {
                val firstComparison = getComparison(firstPair, firstIndex, sign, secondIndex)
                val secondComparison = getComparison(secondPair, firstIndex, sign, secondIndex)

                if (firstComparison == secondComparison) {
                    lToRinstList.add(TransformRule(-firstComparison * -sign, -sign, firstIndex, secondIndex))
                    rToLinstList.add(TransformRule(firstComparison, -sign, secondIndex, firstIndex))
                }
            }
        }
    }

    // Cannot trust the results, try to handle this more gracefully only if it breaks (which it didn't)
    if (lToRinstList.size > 3) {
        return emptyList()
    }

    return listOf(Pair(entry.key, lToRinstList.toList()), Pair(Pair(entry.key.second, entry.key.first), rToLinstList.toList()))
}

private fun getComparison(firstPair: Pair<Pair<Int, Triple<Int, Int, Int>>, Pair<Int, Triple<Int, Int, Int>>>, firstIndex: Int, sign: Int, secondIndex: Int) =
        firstPair.first.second.toList()[firstIndex] + sign * firstPair.second.second.toList()[secondIndex]

private fun addAllMissingTransforms(asInstructions: Map<Pair<Int, Int>, List<TransformRule>>): Map<Pair<Int, Int>, List<TransformRule>> {
    val maxId = asInstructions.keys.flatMap { p -> p.toList() }.toSet().max()

    val result = asInstructions.toMutableMap()

    while (result.keys.size < ((maxId + 1) * (maxId + 2)) / 2) {
        for (i in 0..maxId) {
            for (j in 0..maxId) {
                if (result.containsKey(Pair(i, j))) continue

                val neededTransforms = identifyAddableNeededTransforms(result, i)
                neededTransforms.forEach { (s, m, t) ->
                    result[Pair(s, t)] = mergeTransform(result[Pair(s, m)]!!, result[Pair(m, t)]!!)
                }
                neededTransforms.forEach { (s, m, t) ->
                    result[Pair(t, s)] = mergeTransform(result[Pair(t, m)]!!, result[Pair(m, s)]!!)
                }
            }
        }
    }

    return result.toMap()
}

private fun identifyAddableNeededTransforms(result: MutableMap<Pair<Int, Int>, List<TransformRule>>, i: Int) =
        result.keys.filter { (s, _) -> s == i }
                .flatMap { (_, t) ->
                    result.keys.filter { (s, _) -> s == t }
                            .map { (s2, t2) -> Triple(i, s2, t2) }
                }
                .filter { (s, _, t2) -> s != t2 }
                .filter { (s, _, t) -> !result.containsKey(Pair(s, t)) }

private fun mergeTransform(first: List<TransformRule>, second: List<TransformRule>): List<TransformRule> {
    val result = mutableListOf<TransformRule>()

    for (i in 0..2) {
        val firstRule = first.find { t -> t.target == i }!!
        val secondRule = second.find { t -> t.source == i }!!
        result.add(TransformRule((firstRule.offset * secondRule.sign) + secondRule.offset,
                firstRule.sign * secondRule.sign, firstRule.source, secondRule.target))
    }

    return result.toList()
}

private fun doMerge(idToPoints: Map<Int, List<Triple<Int, Int, Int>>>, asInstructions: Map<Pair<Int, Int>, List<TransformRule>>):
        Set<Triple<Int, Int, Int>> {
    val map = idToPoints.mapValues { (_, v) -> v.toSet() }

    val targetMap = mutableMapOf<Int, Set<Triple<Int, Int, Int>>>()
    for ((k, v) in map.entries) {
        if (k == 0) {
            targetMap.merge(k, v) { v1, v2 -> v1 + v2 }
        } else {
            val transformed = doTransform(v, asInstructions[Pair(k, 0)]!!)
            targetMap.merge(0, transformed) { v1, v2 -> v1 + v2 }
        }
    }

    return targetMap[0]!!
}

private fun doTransform(v: Set<Triple<Int, Int, Int>>, trs: List<TransformRule>): Set<Triple<Int, Int, Int>> {
    return v.map { t ->
        Triple(applyTransform(t, trs.find { r -> r.target == 0 }!!),
                applyTransform(t, trs.find { r -> r.target == 1 }!!),
                applyTransform(t, trs.find { r -> r.target == 2 }!!))
    }
            .toSet()
}

private fun applyTransform(t: Triple<Int, Int, Int>, r: TransformRule): Int {
    return r.offset + t.toList()[r.source] * r.sign
}