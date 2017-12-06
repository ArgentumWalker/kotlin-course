package ru.spbau.mit.evaluation_tree

abstract class Node(val line: Long) {
    abstract fun exec(scope: Scope): Value
}


abstract class Statement(line: Long): Node(line)

abstract class Expression(line: Long): Statement(line)

class Block(val statements: List<Statement>, line: Long): Node(line) {
    override fun exec(scope: Scope): Value {
        scope.put()
        try {
            for (statement in statements) {
                statement.exec(scope)
            }
        } finally {
            scope.popup()
        }
        return Value(0, line)
    }
}

//Statements

class While(private val expr: Expression, val block: Block, line: Long): Statement(line) {
    override fun exec(scope: Scope): Value {
        while (expr.exec(scope).value != 0L) {
            block.exec(scope)
        }
        return Value(0, line)
    }
}

class If(private val expr: Expression, val block: Block, val elseBlock: Block?, line: Long): Statement(line) {
    override fun exec(scope: Scope): Value {
        if (expr.exec(scope).value != 0L) {
            block.exec(scope)
        } else {
            elseBlock?.exec(scope)
        }
        return Value(0, line)
    }
}

class Return(private val expr: Expression, line: Long): Statement(line) {
    override fun exec(scope: Scope): Value {
        throw ReturnException(expr.exec(scope), line)
    }
}

//Definitions
class Parameters(val parametrs: List<String>, line: Long): Node(line) {
    override fun exec(scope: Scope): Value {
        throw FakeNodeException(line)
    }
}

open class Function(val name: String, val parameters: Parameters, val block: Block, line: Long): Statement(line) {
    override fun exec(scope: Scope): Value {
        scope.addFunction(this, line)
        return Value(0, line)
    }
}

class PrintFunction(name: String): Function(name, Parameters(emptyList(), -1), Block(emptyList(), -1), -1) {
    override fun exec(scope: Scope): Value {throw AlreadyDefinedException(name, line)}
}

class Variable(val name: String, val expr: Expression?, line: Long): Statement(line) {
    var value: Value = Value(0, line)

    override fun exec(scope: Scope): Value {
        if (expr != null) {
            value = expr.exec(scope)
        }
        scope.addVariable(this, line)
        return Value(0, line)
    }
}

//Calls
class Arguments(val arguments: List<Expression>, line: Long): Node(line) {
    override fun exec(scope: Scope): Value {
        throw FakeNodeException(line)
    }
}

class FunctionCall(private val name: String, val arguments: Arguments, line: Long): Expression(line) {
    override fun exec(scope: Scope): Value {
        val f: Function = scope.getFunction(name, line)
        if (f is PrintFunction) {
            for (arg in arguments.arguments) {
                print(arg)
            }
            println()
            return Value(0, line)
        }
        if (f.parameters.parametrs.size != arguments.arguments.size) {
            throw WrongArgumentCountException(line)
        }
        val vars: MutableList<Variable> = mutableListOf()
        for (i: Int in 0 until arguments.arguments.size) {
            vars.add(Variable(f.parameters.parametrs[i], arguments.arguments[i].exec(scope), line))
        }
        scope.put()
        try {
            for (v in vars) {
                scope.addVariable(v, line)
            }
            f.block.exec(scope)
        } catch (e: ReturnException) {
            return e.value
        } catch (e: Exception) {
            throw e
        } finally {
            scope.popup()
        }
        return Value(0, line)
    }
}

class VariableCall(private val name: String, line: Long): Expression(line) {
    override fun exec(scope: Scope): Value = scope.getVariable(name, line).value
}

class VariableValueAssign(
        private val name: String,
        private val expr: Expression,
        line: Long
): Expression(line) {
    override fun exec(scope: Scope): Value {
        val value: Value = expr.exec(scope)
        val variable: Variable = scope.getVariable(name, line)
        variable.value.value = value.value
        return value
    }
}

class VariableAssign(
        private val name: String,
        private val expr: Expression,
        line: Long
): Expression(line) {
    override fun exec(scope: Scope): Value {
        val value: Value = expr.exec(scope)
        val variable: Variable = scope.getVariable(name, line)
        variable.value = value
        return value
    }
}

//Values
class BinaryExpression(
        val left: Expression,
        private val type: OperationType,
        val right: Expression,
        line: Long
): Expression(line) {
    override fun exec(scope: Scope): Value {
        val l: Value = left.exec(scope)
        val r: Value = right.exec(scope)
        return l.eval(r, type)
    }
}

class Value(var value: Long, line: Long): Expression(line) {
    override fun exec(scope: Scope): Value = this

    private fun plus(v: Value): Value = Value(value + v.value, line)
    private fun minus(v: Value): Value = Value(value - v.value, line)
    private fun mult(v: Value): Value = Value(value * v.value, line)

    private fun divide(v: Value): Value = Value(value / v.value, line)
    private fun mod(v: Value): Value = Value(value % v.value, line)

    private fun or(v: Value): Value = if (value != 0L || v.value != 0L) Value(1, line) else Value(0, line)
    private fun and(v: Value): Value = if (value != 0L && v.value != 0L) Value(1, line) else Value(0, line)

    private fun less(v: Value): Value = if (value < v.value) Value(1, line) else Value(0, line)
    private fun leq(v: Value): Value = if (value <= v.value) Value(1, line) else Value(0, line)
    private fun gret(v: Value): Value = if (value > v.value) Value(1, line) else Value(0, line)
    private fun geq(v: Value): Value = if (value >= v.value) Value(1, line) else Value(0, line)
    private fun eq(v: Value): Value = if (value  == v.value) Value(1, line) else Value(0, line)
    private fun neq(v: Value): Value = if (value != v.value) Value(1, line) else Value(0, line)

    fun eval(v: Value, op: OperationType): Value {
        return when (op) {
            OperationType.PLUS -> plus(v)
            OperationType.MINUS -> minus(v)
            OperationType.MULT -> mult(v)
            OperationType.DIVIDE -> divide(v)
            OperationType.MOD -> mod(v)
            OperationType.OR -> or(v)
            OperationType.AND -> and(v)
            OperationType.LESS -> less(v)
            OperationType.LEQ -> leq(v)
            OperationType.GRET -> gret(v)
            OperationType.GEQ -> geq(v)
            OperationType.EQ -> eq(v)
            OperationType.NEQ -> neq(v)
        }
    }
}

enum class OperationType {
    PLUS,
    MINUS,

    MULT,
    DIVIDE,
    MOD,

    OR,
    AND,

    LESS,
    LEQ,
    GRET,
    GEQ,
    EQ,
    NEQ
}