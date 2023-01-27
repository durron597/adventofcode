package org.jared.twentyone.daytwentythree

data class Path(val source: Int, val target: Int, val cost: Int, val between: Set<Int>)