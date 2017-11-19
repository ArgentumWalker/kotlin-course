package ru.spbau.mit.tex_markup

class TextModeCustomTag(
        override val name: String,
        override val text: String?,
        override val args: List<Pair<String, String>>
): TexTag() {
    override val container: TextMode = TextMode()
    fun get(): TextMode = container
}

class MathModeCustomTag(
        override val name: String,
        override val text: String?,
        override val args: List<Pair<String, String>>
): TexTag() {
    override val container: MathMode = MathMode()
    fun get(): MathMode = container
}

fun TextMode.customTag(
        name: String,
        title: String? = null,
        vararg args: Pair<String, String>,
        init: TextMode.() -> Unit
) {
    val tag = TextModeCustomTag(name, title, args.toList())
    tag.get().init()
    +tag.build()
}

fun MathMode.customTag(
        name: String,
        title: String? = null,
        vararg args: Pair<String, String>,
        init: MathMode.() -> Unit
) {
    val tag = MathModeCustomTag(name, title, args.toList())
    tag.get().init()
    +tag.build()
}