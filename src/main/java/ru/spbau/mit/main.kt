package ru.spbau.mit

import ru.spbau.mit.evaluation_tree.EvaluationTree
import ru.spbau.mit.evaluation_tree.EvaluationTreeBuilderVisitor
import ru.spbau.mit.evaluation_tree.Value
import java.io.File

fun main(args: Array<String>) {

    if (args.size != 1) {
        println("Wrong number of arguments")
        return
    }
    try {
        val res: Value = EvaluationTreeBuilderVisitor.buildTree(File(args[0])).evaluate()
        println("Completed with exitcode " + res.value)
    } catch (e: Exception) {
        println(e.message)
    }
}
