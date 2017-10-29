package ru.spbau.mit.svidchenko.hw01

import java.util.*

fun main(args: Array<String>) {
    readLine()
    val inputData: List<Int> = readLine()!!.split(" ").map(String::toInt)
    val edges: MutableMap<Int, Int> = HashMap()
    for (i: Int in 0..inputData.size - 1) {
        edges.put(i + 1, inputData[i])
    }
    print(solve(edges))
}

fun solve(edges: Map<Int, Int>): Long {
    val cycleValues: List<Long> = CyclicGraph(edges).cycles.map {it -> it.length().toLong()}
    var sum: Long = 0
    cycleValues.forEach{it -> sum += it * it}
    var first: Long = 0
    var second: Long = 0
    for (value in cycleValues) {
        if (value > first) {
            second = first
            first = value
            continue
        }
        if (value > second) {
            second = value
            continue
        }
    }
    sum += 2 * first * second
    return sum
}

class CyclicGraph {
    val cycles: ArrayList<Cycle> = ArrayList()

    constructor(edges: Map<Int, Int>) {
        val used: MutableMap<Int, Boolean> = HashMap()
        for ((key) in edges) {
            val bool: Boolean = used[key] ?: false
            if (!bool) {
                val cycle:Cycle = Cycle(edges, key)
                cycles.add(cycle)
                cycle.vertexes.forEach {it -> used.put(it, true)}
            }
        }
    }
}

class Cycle {
    val edges: Map<Int, Int>
    val vertexes: ArrayList<Int> = ArrayList()

    constructor(edges: Map<Int, Int>, startVertex: Int) {
        this.edges = edges
        vertexes.add(startVertex)
        var vertex: Int? = edges[startVertex]
        while (vertex != startVertex && vertex != null) {
            vertexes.add(vertex)
            vertex = edges[vertex]
        }
    }

    fun length(): Int {
        return vertexes.size
    }
}