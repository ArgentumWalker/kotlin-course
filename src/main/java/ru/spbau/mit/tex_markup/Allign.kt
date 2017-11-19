package ru.spbau.mit.tex_markup

class Align(override val name: String): TexTag() {
    override val container: TextMode = TextMode()
    fun get(): TextMode = container
}

fun TextMode.left(init: TextMode.() -> Unit) {
    val align = Align("left")
    align.get().init()
    +align.build()
}

fun TextMode.center(init: TextMode.() -> Unit) {
    val align = Align("center")
    align.get().init()
    +align.build()
}

fun TextMode.right(init: TextMode.() -> Unit) {
    val align = Align("right")
    align.get().init()
    +align.build()
}