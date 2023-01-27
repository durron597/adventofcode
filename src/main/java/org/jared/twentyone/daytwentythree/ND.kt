package org.jared.twentyone.daytwentythree

data class ND (val node: List<Int>, val d: Int, var prev: ND?, var f: Double = Double.POSITIVE_INFINITY, var g: Double = Double.POSITIVE_INFINITY) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ND

        if (node != other.node) return false

        return true
    }

    override fun hashCode(): Int {
        return node.hashCode()
    }
}