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
open class BaseMode: ResultContainer()

open class TextMode : BaseMode()

open class MathMode : BaseMode()

@DslMarker
annotation class TexTextMarker
