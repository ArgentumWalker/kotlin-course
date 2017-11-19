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
        if (text != null) {
            return "{$text}"
        }
        return ""
    }

    private fun buildArgs(): String {
        if (!args.isEmpty()) {
            val builder = StringBuilder()
            for ((k, v) in args) {
                if (k.isEmpty()) {
                    builder.append("$v, ")
                } else {
                    builder.append("$k = $v, ")
                }
            }
            builder.delete(builder.length-2, builder.length)
            return "[" + builder.toString() + "]"
        }
        return ""
    }
}