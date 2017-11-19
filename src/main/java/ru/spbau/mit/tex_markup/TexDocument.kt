package ru.spbau.mit.tex_markup

/**
 * Document base. Here you can set class of your document, add packages and create frames
 */
class TexDocument: TextMode() {
    var clazz: String = ""
    var packagesBuilder: StringBuilder = StringBuilder()

    fun documentClass(type: String) {
        clazz = "\\documentclass{$type}\n"
    }

    fun usepackage(name: String, vararg args: String) {
        packagesBuilder.append("\\usepackage{$name}")
        if (!args.isEmpty()) {
            val builder = StringBuilder()
            for (v in args) {
                builder.append("$v, ")
            }
            builder.delete(builder.length-2, builder.length)
            packagesBuilder.append("[").append(builder.toString()).append("]")
        }
        packagesBuilder.append("\n")
    }

    fun frame(title: String?, vararg args: Pair<String, String>, init: TextMode.() -> Unit) {
        val frame = TextModeCustomTag("frame", title, args.toList())
        frame.get().init()
        content.append(frame.build())
    }
}

fun document(init: TexDocument.() -> Unit): String {
    val doc: TexDocument = TexDocument()
    doc.init()
    return doc.clazz +
            doc.packagesBuilder.toString() +
            "\n\\begin{document}\n" +
            doc.content.toString() +
            "\n\\end{document}\n"
}