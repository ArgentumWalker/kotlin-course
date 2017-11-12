package ru.spbau.mit.evaluation_tree

import org.junit.Test

import org.junit.Assert.*

class EvaluationTreeTest {
    val tests: List<String> = listOf(
            "1",
            "0",
            "10",
            "-1",
            "var a",
            "var aba",
            "var a_",
            "var a0",
            "1 + 1",
            "1 - 1",
            "1 * 1",
            "1 / 1",
            "1 % 1",
            "1 || 1",
            "1 && 1",
            "1 == 1",
            "1 < 1",
            "1 <= 1",
            "1 != 1",
            "1 >= 1",
            "1 > 1",
            "(1 + 1)",
            "((1 + 1))",
            "(1 + 1) + (1 + 1)",
            "if (1) {1}",
            "if (1) {1} else {0}",
            "while (0) {1}",
            "var a",
            "var a = 2",
            "var a a = 2",
            "var a = 2 while (a) {a = a - 1}",
            "fun foo() {2}",
            "fun foo() {return 2}",
            "fun foo(a) {return a}",
            "fun foo() {return 2} foo()",
            "fun foo(a) {return a} foo(2)"
    )

    @Test
    fun parseTest() {
        tests.forEach { s -> println(s); EvaluationTree(s); }
    }

    @Test
    fun evaluateTest() {
        //tests.forEach { s -> println(s); EvaluationTree(s).evaluate(); }
    }

}