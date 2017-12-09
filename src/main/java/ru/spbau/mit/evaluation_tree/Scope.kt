package ru.spbau.mit.evaluation_tree

import java.util.*

class Scope {
    private val varDeque : Holder<Variable> = Holder()
    private val funDeque : Holder<Function> = Holder()

    companion object {
        fun defaultScope(): Scope {
            val res: Scope = Scope()
            res.put()
            res.addFunction(PrintFunction("print"), -1)
            return res
        }
    }

    fun getFunction(name: String, line: Long): Function = funDeque.get(name, line)

    fun addFunction(f: Function, line: Long) = funDeque.add(f, f.name, line)

    fun getVariable(name: String, line: Long): Variable = varDeque.get(name, line)

    fun addVariable(v: Variable, line: Long) = varDeque.add(v, v.name, line)

    fun popup() {
        varDeque.popup()
        funDeque.popup()
    }
    fun put() {
        varDeque.put()
        funDeque.put()
    }

    class Holder<T> {
        private val scopeDeque : Deque<MutableMap<String, T>> = LinkedList<MutableMap<String, T>>()

        fun put() {
            scopeDeque.addFirst(mutableMapOf())
        }

        fun popup() {
            scopeDeque.removeFirst()
        }

        fun add(elem: T, name: String, line: Long) {
            if (scopeDeque.first.containsKey(name)) {
                throw AlreadyDefinedException(name, line)
            }
            scopeDeque.first.put(name, elem)
        }

        fun get(name: String, line: Long): T {
            for (map in scopeDeque) {
                val res: T? = map[name]
                if (res != null) {
                    return res
                }
            }
            throw NoSuchException(name, line)
        }
    }
}