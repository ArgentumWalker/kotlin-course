package ru.spbau.mit.evaluation_tree

import java.util.*

class Scope {
    private val varDeque : Deque<MutableMap<String, Variable>> = LinkedList<MutableMap<String, Variable>>()
    private val funDeque : Deque<MutableMap<String, Function>> = LinkedList<MutableMap<String, Function>>()

    companion object {
        fun defaultScope(): Scope {
            val res: Scope = Scope()
            res.put()
            res.addFunction(PrintFunction("print"), -1)
            return res
        }
    }

    fun getFunction(name: String, line: Long): Function {
        for (map in funDeque) {
            val res: Function? = map[name]
            if (res != null) {
                return res
            }
        }
        throw NoSuchFunctionException(name, line)
    }

    fun addFunction(f: Function, line: Long) {
        if (varDeque.first.containsKey(f.name)) {
            throw VariableAlreadyDefinedException(f.name, line)
        }
        funDeque.first.put(f.name, f)
    }

    fun getVariable(name: String, line: Long): Variable {
        for (map in varDeque) {
            val res: Variable? = map[name]
            if (res != null) {
                return res
            }
        }
        throw NoSuchVariableException(name, line)
    }

    fun addVariable(v: Variable, line: Long) {
        if (varDeque.first.containsKey(v.name)) {
            throw VariableAlreadyDefinedException(v.name, line)
        }
        varDeque.first.put(v.name, v)
    }

    fun popup() {
        varDeque.removeFirst()
        funDeque.removeFirst()
    }
    fun put() {
        varDeque.addFirst(mutableMapOf())
        funDeque.addFirst(mutableMapOf())
    }
}