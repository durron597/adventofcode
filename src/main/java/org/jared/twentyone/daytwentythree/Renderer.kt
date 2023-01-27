package org.jared.twentyone.daytwentythree

class Renderer {
    fun printRecurse(nd: ND) {
        if (nd.prev != null) {
            printRecurse(nd.prev!!)
        }
        renderNd(nd)
    }

    fun renderNd(state: ND) {
        renderState(state.node)
    }

    fun renderState(state: List<Int>) {
        val roomSize = computeRoomSize(state.size)
        println("#############")
        print("#")
        print(vp(state[roomSize * 4]))
        print(vp(state[roomSize * 4 + 1]))
        for (i in roomSize * 4 + 4..roomSize * 4 + 6) {
            print(".")
            print(vp(state[i]))
        }
        print(".")
        print(vp(state[roomSize * 4 + 3]))
        print(vp(state[roomSize * 4 + 2]))
        println("#")
        for (i in roomSize - 1 downTo 0) {
            print(if (i == roomSize - 1) "##" else "  ")
            print("#")
            for(j in 0..roomSize * 3 step roomSize) {
                print(vp(state[i + j]))
                print("#")
            }
            if (i == roomSize - 1) println("##") else println()
        }
        println("  #########")
    }

    private fun computeRoomSize(listSize: Int): Int {
        return (listSize - 7) / 4
    }

    private fun vp(value: Int): String {
        return when (value) {
            1 -> "A"
            2 -> "B"
            3 -> "C"
            4 -> "D"
            else -> { "." }
        }
    }
}