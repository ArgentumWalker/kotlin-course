package ru.spbau.mit.tex_markup

abstract class TexTag: TexRoot() {
    protected abstract val name: String
    protected open val text: String? = null
    protected open val args: List<Pair<String, String>> = listOf()
    protected abstract val container: ResultContainer

    fun build(): String {
        return "\n\\begin{$name}" + buildText() + buildArgs() + "\n" +
                container.build() +
                "\n\\end{$name}\n"
    }

    private fun buildText(): String {
        return if (text != null) "{$text}" else ""
    }

    private fun buildArgs(): String {
        return if (!args.isEmpty())
            args.joinToString(transform = {pair -> pair.first + " = " + pair.second}, prefix = "[", postfix = "]")
            else ""
    }
}