package ru.spbau.mit.evaluation_tree

open class LanguageException(val line: Long): Exception()

class NoSuchFunctionException(val name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: No function with name \"$name\" found."
}

class NoSuchVariableException(val name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: No variable with name \"$name\" found."
}

class FunctionAlreadyDefinedException(val name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Function with name \"$name\" is already defined."
}

class VariableAlreadyDefinedException(val name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Variable with name \"$name\" is already defined."
}

class FakeNodeException(line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Can't evaluate fake node"
}

class ParsingException(line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Parsing exception"
}

class WrongArgumentCountException(line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Wrong argument count"
}

class ReturnException(val value: Value, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: Return not allowed here"
}