package ru.spbau.mit.tex_markup

class Math: TexTag() {
    override val name: String = "math"
    override val container: MathMode = MathMode()
    fun get(): MathMode = container
}

fun TextMode.math(init: MathMode.() -> Unit) {
    val math = Math()
    math.get().init()
    +math.build()
}