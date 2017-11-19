package ru.spbau.mit.tex_markup

open class ResultContainer: TexRoot() {
    val content: StringBuilder = StringBuilder()

    fun build(): String {
        return content.toString();
    }

    operator fun String.unaryPlus() {
        content.append(this)
    }
}

@TexTextMarker
open class MathMode: ResultContainer()

open class TextMode : MathMode()

@DslMarker
annotation class TexTextMarker
