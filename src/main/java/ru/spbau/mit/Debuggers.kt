package ru.spbau.mit

class ConsoleDebugger: Debugger() {
    suspend override fun startWithSuspend() {

        while (true) {
            try {
                if (currentDebugVisitor != null) {
                    if (currentDebugVisitor!!.isFinished()) {
                        println("Program execution completed with " + currentDebugVisitor!!.result()!!.value)
                    } else {
                        println("Now on line " + currentDebugVisitor!!.currentLine())
                    }
                }
                val input: String? = readLine()
                if (input != null) {
                    if (input.startsWith("load")) {
                        load(input.drop(5))
                    }
                    if (input.startsWith("breakpoint")) {
                        breakpoint(input.drop(11).toLong())
                    }
                    if (input.startsWith("remove")) {
                        removeBreakpoint(input.drop(7).toLong())
                    }
                    if (input == "run") {
                        if (currentDebugVisitor != null) {
                            println("Already started")
                            continue
                        }
                        if (currentEvaluationTree == null) {
                            println("Nothing to run")
                            continue
                        }
                        run()
                    }
                    if (input == "stop") {
                        stop()
                    }
                    if (input == "continue") {
                        if (currentDebugVisitor != null && !currentDebugVisitor!!.isFinished()) {
                            continueRun()
                        } else {
                            println("Nothing to continue")
                        }
                    }
                    if (input.startsWith("condition")) {
                        val s = input.drop(10)
                        val line = s.substring(0, s.indexOf(' ')).toLong()
                        val condition = s.drop(s.indexOf(' ') + 1)
                        condition(line, condition)
                    }
                    if (input.startsWith("evaluate")) {
                        if (currentDebugVisitor == null) {
                            println("No program running")
                            continue
                        }
                        println("Result: " + evaluate(input.drop(9)))
                    }
                    if (input == "list") {
                        for (breakpoint in breakpointsDescription) {
                            println(breakpoint.key.toString() +
                                    (if (breakpoint.value != "") " :: " + breakpoint.value else ""))
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

}

class TestDebugger(private var action: suspend TestDebugger.() -> Unit): Debugger() {

    suspend fun finished(): Boolean {
        return currentDebugVisitor!!.isFinished()
    }

    suspend fun currentLine(): Long {
        return currentDebugVisitor!!.currentLine()
    }

    suspend override fun startWithSuspend() {
        action.invoke(this)
    }
}