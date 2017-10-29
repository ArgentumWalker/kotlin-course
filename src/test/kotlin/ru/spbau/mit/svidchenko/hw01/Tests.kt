package ru.spbau.mit.svidchenko.hw01

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import java.util.*

class Tests {
    //1 cycle, answer 1
    val edges1: MutableMap<Int, Int> = HashMap()
    //1 cycle, answer 4
    val edges2: MutableMap<Int, Int> = HashMap()
    //2 cycles, answer 9
    val edges3: MutableMap<Int, Int> = HashMap()
    //3 cycles, answer 17
    val edges4: MutableMap<Int, Int> = HashMap()

    @Before
    fun before() {
        edges1.put(1, 1)
        edges2.put(1, 2)
        edges2.put(2, 1)
        edges3.put(1, 2)
        edges3.put(2, 1)
        edges3.put(3, 3)
        edges4.put(1, 2)
        edges4.put(2, 1)
        edges4.put(3, 3)
        edges4.put(4, 5)
        edges4.put(5, 4)
    }

    @Test
    fun solveTest() {
        assertEquals(1, solve(edges1))
        assertEquals(4, solve(edges2))
        assertEquals(9, solve(edges3))
        assertEquals(17, solve(edges4))
    }

    @Test
    fun circleBuilder() {
        var cycle: Cycle
        cycle = Cycle(edges1, 1)
        assertArrayEquals(IntArray(1, {1}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges2, 1)
        assertArrayEquals(IntArray(2, {it -> it + 1}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges3, 1)
        assertArrayEquals(IntArray(2, {it -> it + 1}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges3, 3)
        assertArrayEquals(IntArray(1, {3}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges4, 1)
        assertArrayEquals(IntArray(2, {it -> it + 1}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges4, 3)
        assertArrayEquals(IntArray(1, {3}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
        cycle = Cycle(edges4, 4)
        assertArrayEquals(IntArray(2, {it -> it + 4}), IntArray(cycle.vertexes.size, {it -> cycle.vertexes[it]}))
    }

    @Test
    fun circleGraph() {
        assertEquals(1, CyclicGraph(edges1).cycles.size)
        assertEquals(1, CyclicGraph(edges2).cycles.size)
        assertEquals(2, CyclicGraph(edges3).cycles.size)
        assertEquals(3, CyclicGraph(edges4).cycles.size)
    }

}