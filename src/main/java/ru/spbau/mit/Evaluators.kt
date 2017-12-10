package ru.spbau.mit

import ru.spbau.mit.evaluation_tree.*
import java.io.File
import kotlin.coroutines.experimental.*

abstract class Suspendable {
    private var continuation: Continuation<Unit>? = null

    protected fun reset(action: suspend () -> Unit) {
        continuation = action.createCoroutine(object : Continuation<Unit> {
            override val context: CoroutineContext = EmptyCoroutineContext
            override fun resumeWithException(exception: Throwable) { throw exception }
            override fun resume(value: Unit) {}
        })
    }

    fun resume() {
        continuation?.resume(Unit)
    }

    suspend fun suspend(toResume: Suspendable) {
        suspendCoroutine<Unit> { continuation ->
            this.continuation = continuation
            toResume.resume()
        }
    }
}

@RestrictsSuspension
abstract class EvaluationVisitor(
        val scope: Scope,
        protected val returnTo: Suspendable
): Suspendable() {
    private var finished : Boolean = false
    private var result: Value? = null

    fun isFinished() = finished
    fun result() = result

    suspend fun evaluate(node: Node) {
        finished = false
        reset { result = visit(node); finished = true; suspend(returnTo) }
        returnTo.suspend(this@EvaluationVisitor)
    }
    abstract suspend fun visit(node: Node) : Value
}

@RestrictsSuspension
abstract class Evaluator: Suspendable() {

    protected abstract suspend fun startWithSuspend()

    fun start() {
        reset { startWithSuspend() }
        resume()
    }
}


class Runner: Evaluator() {
    private val visitor: EvaluationVisitor = RunVisitor(Scope.defaultScope(), this@Runner)
    var tree: EvaluationTree? = null

    suspend override fun startWithSuspend() {
        tree?.rootNode?.let { visitor.evaluate(it) }
    }
}

abstract class Debugger: Evaluator() {
    protected var currentDebugVisitor: DebugVisitor? = null
    protected var currentEvaluationTree: EvaluationTree? = null
    protected val breakpoints: MutableMap<Long, Expression> = HashMap()
    protected val breakpointsDescription: MutableMap<Long, String> = HashMap()

    suspend fun load(filename: String) {
        val f: File = File(filename)
        currentEvaluationTree = EvaluationTreeBuilderVisitor.buildTree(f)
        currentDebugVisitor = null
        breakpoints.clear()
        breakpointsDescription.clear()
    }

    suspend fun breakpoint(line: Long) {
        breakpoints.put(line, Value(1, -1))
        breakpointsDescription.put(line, "")
    }

    suspend fun condition(line: Long, code: String) {
        breakpoints.put(line, EvaluationTreeBuilderVisitor.buildExpression(code))
        breakpointsDescription.put(line, code)
    }

    suspend fun continueRun() {
        if (currentDebugVisitor != null && !currentDebugVisitor!!.isFinished()) {
            suspend(currentDebugVisitor!!)
        }
    }

    suspend fun stop() {
        currentDebugVisitor = null
    }

    suspend fun run() {
        if (currentDebugVisitor != null || currentEvaluationTree == null) {
            return
        }
        currentDebugVisitor = DebugVisitor(Scope.defaultScope(), breakpoints, this@Debugger)
        currentDebugVisitor!!.evaluate(currentEvaluationTree!!.rootNode)
    }

    suspend fun removeBreakpoint(line: Long) {
        breakpoints.remove(line)
        breakpointsDescription.remove(line)
    }

    suspend fun evaluate(expression: String): Value {
        return currentDebugVisitor!!.runner.visit(EvaluationTreeBuilderVisitor.buildExpression(expression))
    }
}

class RunVisitor(scope: Scope, returnTo: Suspendable): EvaluationVisitor(scope, returnTo) {
    suspend override fun visit(node: Node): Value {
        return node.exec(scope, this)
    }
}

class DebugVisitor(scope: Scope,
                   private val breakPoints: Map<Long, Expression>,
                   returnTo: Suspendable
): EvaluationVisitor(scope, returnTo) {
    private var currentLine: Long = -1
    val runner = RunVisitor(scope, returnTo)

    fun currentLine(): Long = currentLine

    suspend override fun visit(node: Node): Value {
        if (node.line != currentLine
                && breakPoints.contains(node.line)
                && runner.visit(breakPoints.getValue(node.line)).value != 0L) {
            currentLine = node.line
            suspend(returnTo)
        }
        currentLine = node.line
        return node.exec(scope, this)
    }
}
