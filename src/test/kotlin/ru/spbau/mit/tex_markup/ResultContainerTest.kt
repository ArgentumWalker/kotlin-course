package ru.spbau.mit.tex_markup

import org.junit.Test

import org.junit.Assert.*

class ResultContainerTest {

    private fun ResultContainer.testFun(s: String) {
        +s
    }

    @Test
    fun test() {
        val resultContainer = ResultContainer()
        resultContainer.testFun("foo")
        assertEquals("foo", resultContainer.build())
        resultContainer.testFun("bar")
        assertEquals("foobar", resultContainer.build())
    }

}