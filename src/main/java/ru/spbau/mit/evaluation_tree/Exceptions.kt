package ru.spbau.mit.evaluation_tree

open class LanguageException(val line: Long): Exception()

class NoSuchException(name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: \"$name\" not found."
}

class AlreadyDefinedException(name: String, line: Long): LanguageException(line) {
    override val message: String? = line.toString() + ":: \"$name\" already defined."
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