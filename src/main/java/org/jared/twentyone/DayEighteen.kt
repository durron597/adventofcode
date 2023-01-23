package org.jared.twentyone

import io.vavr.collection.List
import io.vavr.control.Either
import io.vavr.control.Option
import io.vavr.control.Option.none
import io.vavr.control.Option.some
import org.jared.util.ScanIterator
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.ceil
import kotlin.math.floor

fun main(args: Array<String>) {
    val iter = ScanIterator(Scanner(DayOne::class.java.getResourceAsStream("/2021/day18_input.txt")!!))

    val snailfishNumbers = ArrayDeque<Node>()

    for (str in iter) {
        val nodeStack = ArrayDeque<Node>()
        var idx = 0

        while (idx < str.length) {
            if (str[idx] == ']') {
                val right = nodeStack.removeFirst()
                val left = nodeStack.removeFirst()
                val newNode = Node(null, Either.right(Pair(left, right)))
                left.parent = newNode
                right.parent = newNode
                nodeStack.addFirst(newNode)
            } else if (str[idx].isDigit()) {
                nodeStack.addFirst(Node(null, Either.left(str[idx] - '0')))
            }
            idx++
        }

        val tree = nodeStack.removeFirst()

        snailfishNumbers.addLast(tree)
    }

    val result = partOne(snailfishNumbers)
    println(result.first)
    println(result.second)

    val resultTwo = partTwo(snailfishNumbers)
    println(resultTwo)
}

fun partTwo(snailfishNumbers: ArrayDeque<Node>): Option<Int> {
    val vavrList = List.ofAll(snailfishNumbers)
    val pairedAll = vavrList
            .flatMap { n -> vavrList.map { n2 -> Pair(n, n2) } }
    return pairedAll
            .flatMap { t ->
                (if (t.first === t.second) {
                    none()
                } else {
                    some(sum(t.first.deepCopy(), t.second.deepCopy()))
                })
            }
            .map(::magnitude)
            .max()
}

private fun partOne(inputList: ArrayDeque<Node>): Pair<Node, Int> {
    val snailfishNumbers = ArrayDeque<Node>()
    for (n in inputList) {
        snailfishNumbers.addLast(n.deepCopy())
    }
    while (snailfishNumbers.size > 1) {
        val left = snailfishNumbers.removeFirst()
        val right = snailfishNumbers.removeFirst()
        val newNode = sum(left, right)

        snailfishNumbers.addFirst(newNode)
    }

    return Pair(snailfishNumbers.first(), magnitude(snailfishNumbers.first()))
}

private fun sum(left: Node, right: Node): Node {
    val newNode = Node(null, Either.right(Pair(left, right)))

    left.parent = newNode
    right.parent = newNode

    do {
        var result = findExplode(newNode, 0)
        if (result != null) {
            explode(result)
            continue
        }
        result = findSplit(newNode)
        if (result != null) {
            split(result)
            continue
        }
        break
    } while (true)

    return newNode
}

fun findExplode(node: Node, depth: Int): Node? {
    if (depth == 4 && node.value.isRight) return node
    val checkLeft: Node?
    if (node.value.isRight) {
        checkLeft = findExplode(node.value.get().first, depth + 1)
    } else {
        return null
    }
    return checkLeft ?: findExplode(node.value.get().second, depth + 1)
}

fun findSplit(node: Node): Node? {
    val checkLeft: Node?
    if (node.value.isRight) {
        checkLeft = findSplit(node.value.get().first)
    } else {
        return if (node.value.left > 9) node else null
    }
    return checkLeft ?: findSplit(node.value.get().second)
}

fun split(nodeValue: Node) {
    val value = nodeValue.value.left
    val left = Node(nodeValue, Either.left(floor(value / 2.0).toInt()))
    val right = Node(nodeValue, Either.left(ceil(value / 2.0).toInt()))
    nodeValue.value = Either.right(Pair(left, right))
}

fun successor(nodeValue: Node): Node? {
    var current: Node? = nodeValue
    while (current != null && current.parent?.value?.get()?.first !== current) {
        current = current.parent
    }
    if (current == null) return null
    current = current.parent!!.value.get().second
    while (current!!.value.isRight) {
        current = current.value.get().first
    }
    return current
}

fun predecessor(nodeValue: Node): Node? {
    var current: Node? = nodeValue
    while (current != null && current.parent?.value?.get()?.second !== current) {
        current = current.parent
    }
    if (current == null) return null
    current = current.parent!!.value.get().first
    while (current!!.value.isRight) {
        current = current.value.get().second
    }
    return current
}

fun explode(nodeValue: Node) {
    val pred = predecessor(nodeValue);
    val succ = successor(nodeValue);
    pred?.let { pred.value = Either.left(nodeValue.value.get().first.value.left + (pred.value.left)) }
    succ?.let { succ.value = Either.left(nodeValue.value.get().second.value.left + (succ.value.left)) }

    val zeroNode = Node(nodeValue.parent, Either.left(0))

    val parentsPair = nodeValue.parent!!.value.get()
    if (parentsPair.first === nodeValue) {
        nodeValue.parent!!.value = Either.right(Pair(zeroNode, parentsPair.second))
    } else {
        nodeValue.parent!!.value = Either.right(Pair(parentsPair.first, zeroNode))
    }
}

fun magnitude(node: Node): Int {
    if (node.value.isLeft) {
        return node.value.left
    } else {
        return 3 * magnitude(node.value.get().first) + 2 * magnitude(node.value.get().second)
    }
}

data class Node(var parent: Node?, var value: Either<Int, Pair<Node, Node>>) {
    override fun toString(): String {
        if (value.isLeft) {
            return value.left.toString()
        } else {
            return "[" + value.get().first + "," + value.get().second + "]";
        }
    }

    fun deepCopy(): Node {
        return if (value.isLeft) {
            Node(null, Either.left(value.left))
        } else {
            val first = value.get().first.deepCopy()
            val second = value.get().second.deepCopy()
            val result = Node(null, Either.right(Pair(first, second)))
            first.parent = result
            second.parent = result

            result
        }
    }
}