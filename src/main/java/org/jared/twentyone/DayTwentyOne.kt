package org.jared.twentyone

private fun dayOne() {
    var toAdd = 6

    val loc = intArrayOf(7, 0)
    val score = intArrayOf(0, 0)
    var turn = 0;

    while (score[0] < 1000 && score[1] < 1000) {
        loc[turn and 1] = (loc[turn and 1] + toAdd) % 10
        score[turn and 1] += loc[turn and 1] + 1
        turn += 3
        toAdd--
        if (toAdd < 0) toAdd += 10
    }

    println(turn)
    println(score.min())
    println(turn * score.min())
}

fun dayTwo(loc1: Int, loc2: Int) {
    val result = DayTwentyOne(loc1, loc2).run()
    println(result)
    println(result.toList().max())
}

class DayTwentyOne (private val loc1: Int, private val loc2: Int){
    private val dp = mutableMapOf<DpKey, Pair<Long, Long>>()

    fun run(): Pair<Long, Long> {
        val result = getResult(DpKey(loc1, loc2, 0, 0, true))

        dp.entries.forEach(::println)

        return result
    }

    private fun getResult(key: DpKey): Pair<Long, Long> {
        val result = dp[key]
        if (result != null) return result
        if (key.sc1 >= 21) return Pair(1, 0)
        if (key.sc2 >= 21) return Pair(0, 1)

        val computedResult = getResultNoMemo(key)
        dp[key] = computedResult
        return computedResult
    }

    private fun getResultNoMemo(key: DpKey): Pair<Long, Long> {
        if (key.turn) {
            var total = Pair(0L, 0L)
            for (i in 1 .. 3) {
                for (j in 1 .. 3) {
                    for (k in 1..3) {
                        val newLoc1 = (key.loc1 + i + j + k) % 10
                        val res = getResult(key.copy(loc1 = newLoc1, sc1 = key.sc1 + newLoc1 + 1, turn = false))
                        total = Pair(total.first + res.first, total.second + res.second)
                    }
                }
            }
            return total
        } else {
            var total = Pair(0L, 0L)
            for (i in 1 .. 3) {
                for (j in 1 .. 3) {
                    for (k in 1..3) {
                        val newLoc2 = (key.loc2 + i + j + k) % 10
                        val res = getResult(key.copy(loc2 = newLoc2, sc2 = key.sc2 + newLoc2 + 1, turn = true))
                        total = Pair(total.first + res.first, total.second + res.second)
                    }
                }
            }
            return total
        }
    }
}

fun main() {
//    dayOne()
//    dayTwo(3, 7) // example
    dayTwo(7, 0) // real
}

data class DpKey (val loc1: Int, val loc2: Int, val sc1: Int, val sc2: Int, val turn: Boolean)