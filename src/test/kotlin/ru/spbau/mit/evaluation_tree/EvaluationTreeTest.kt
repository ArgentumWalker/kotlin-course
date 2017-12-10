package ru.spbau.mit.evaluation_tree

import org.junit.Assert
import org.junit.Test

import ru.spbau.mit.Runner

class EvaluationTreeTest {
    private val tests: List<String> = listOf(
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
        tests.forEach { s -> println(s); EvaluationTreeBuilderVisitor.buildTree(s); }
    }

    @Test
    fun evaluateTest() {
        val runner: Runner = Runner()
        tests.forEach {
            s -> println(s)
            runner.tree = EvaluationTreeBuilderVisitor.buildTree(s)
            runner.start()
        }
    }

    @Test
    fun binaryTest() {
        binaryTest1(EvaluationTreeBuilderVisitor.buildTree("1 + 1"))
        binaryTest1(EvaluationTreeBuilderVisitor.buildTree("1 * 1"))
        binaryTest1(EvaluationTreeBuilderVisitor.buildTree("1 || 1"))
        binaryTest1(EvaluationTreeBuilderVisitor.buildTree("1 < 1"))
        val tree = EvaluationTreeBuilderVisitor.buildTree("(1 + 1) + (1 + 1)")
        Assert.assertTrue(tree.rootNode is Block)
        Assert.assertEquals(1, (tree.rootNode as Block).statements.size)
        Assert.assertTrue((tree.rootNode as Block).statements[0] is BinaryExpression)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as BinaryExpression).left is BinaryExpression)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as BinaryExpression).right is BinaryExpression)
        Assert.assertTrue((((tree.rootNode as Block).statements[0] as BinaryExpression).left as BinaryExpression)
                .left is Value)
        Assert.assertTrue((((tree.rootNode as Block).statements[0] as BinaryExpression).left as BinaryExpression)
                .right is Value)
        Assert.assertTrue((((tree.rootNode as Block).statements[0] as BinaryExpression).right as BinaryExpression)
                .left is Value)
        Assert.assertTrue((((tree.rootNode as Block).statements[0] as BinaryExpression).right as BinaryExpression)
                .right is Value)
    }

    private fun binaryTest1(tree : EvaluationTree) {
        Assert.assertEquals(1, (tree.rootNode as Block).statements.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as BinaryExpression).left is Value)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as BinaryExpression).right is Value)
    }

    @Test
    fun ifTest() {
        ifTestCommon(EvaluationTreeBuilderVisitor.buildTree("if (1) {1}"))
        val tree = EvaluationTreeBuilderVisitor.buildTree("if (1) {1} else {0}")
        ifTestCommon(tree)
        Assert.assertEquals(1, ((tree.rootNode as Block).statements[0] as If).elseBlock!!.statements.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as If).elseBlock!!.statements[0] is Value)
    }

    private fun ifTestCommon(tree : EvaluationTree) {
        Assert.assertEquals(1, (tree.rootNode as Block).statements.size)
        Assert.assertEquals(1, ((tree.rootNode as Block).statements[0] as If).block.statements.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as If).block.statements[0] is Value)
    }


    @Test
    fun whileTest() {
        val tree = EvaluationTreeBuilderVisitor.buildTree("while (0) {1}")
        Assert.assertEquals(1, (tree.rootNode as Block).statements.size)
        Assert.assertEquals(1, ((tree.rootNode as Block).statements[0] as While).block.statements.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as While).block.statements[0] is Value)
    }

    @Test
    fun funAndVarTest() {
        val tree = EvaluationTreeBuilderVisitor.buildTree("fun foo(a) {return a} \n var a = 2 \n foo(a)")
        Assert.assertEquals(3, (tree.rootNode as Block).statements.size)

        Assert.assertEquals(1, ((tree.rootNode as Block).statements[0] as Function).parameters.parametrs.size)
        Assert.assertEquals(1, ((tree.rootNode as Block).statements[0] as Function).block.statements.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[0] as Function).block.statements[0] is Return)

        Assert.assertNotNull(((tree.rootNode as Block).statements[1] as Variable).expr)
        Assert.assertTrue(((tree.rootNode as Block).statements[1] as Variable).expr is Value)

        Assert.assertEquals(1, ((tree.rootNode as Block).statements[2] as FunctionCall).arguments.arguments.size)
        Assert.assertTrue(((tree.rootNode as Block).statements[2] as FunctionCall).arguments.arguments[0] is VariableCall)
    }
}