package ru.spbau.mit.tex_markup

import org.junit.Test

import org.junit.Assert.*

class TagTest {

    @Test
    fun simpleTest() {
        assertEquals("\n\\begin{document}\n\n\\begin{left}\n\n\\end{left}\n\n\\end{document}\n", document{left{}})
        assertEquals("\n\\begin{document}\n\n\\begin{right}\n\n\\end{right}\n\n\\end{document}\n", document{right{}})
        assertEquals("\n\\begin{document}\n\n\\begin{center}\n\n\\end{center}\n\n\\end{document}\n", document{center{}})
        assertEquals("\n\\begin{document}\n\n\\begin{itemize}\n\n\\end{itemize}\n\n\\end{document}\n", document{itemize{}})
        assertEquals("\n\\begin{document}\n\n\\begin{enumerate}\n\n\\end{enumerate}\n\n\\end{document}\n", document{enumerate{}})
        assertEquals("\n\\begin{document}\n\n\\begin{math}\n\n\\end{math}\n\n\\end{document}\n", document{math{}})

        assertEquals("\n\\begin{document}\n\n\\begin{abacada}\n\n\\end{abacada}\n\n\\end{document}\n",
                document{customTag("abacada"){}})
        assertEquals("\n\\begin{document}\n\n\\begin{math}\n\n\\begin{abacada}\n\n\\"
                + "end{abacada}\n\n\\end{math}\n\n\\end{document}\n", document{math{customTag("abacada"){}}})

        assertEquals("\n\\begin{document}\n\n\\begin{abacada}{title}\n\n\\end{abacada}\n\n\\end{document}\n",
                document{customTag("abacada", "title"){}})
        assertEquals("\n\\begin{document}\n\n\\begin{math}\n\n\\begin{abacada}{title}\n\n\\"
                + "end{abacada}\n\n\\end{math}\n\n\\end{document}\n", document{math{customTag("abacada", "title"){}}})

        assertEquals("\n\\begin{document}\n\n\\begin{abacada}[key = value]\n\n\\end{abacada}\n\n\\end{document}\n",
                document{customTag("abacada", null, Pair("key", "value")){}})
        assertEquals("\n\\begin{document}\n\n\\begin{math}\n\n\\begin{abacada}[key = value]\n\n\\"
                + "end{abacada}\n\n\\end{math}\n\n\\end{document}\n",
                document{math{customTag("abacada", null, Pair("key", "value")){}}})

        assertEquals("\\usepackage{foo}\n\n\\begin{document}\n\n\\end{document}\n",
                document{usepackage("foo")})
        assertEquals("\\usepackage{foo}[bar]\n\n\\begin{document}\n\n\\end{document}\n",
                document{usepackage("foo", "bar")})
        assertEquals("\\documentclass{foo}\n\n\\begin{document}\n\n\\end{document}\n",
                document{documentClass("foo")})
        assertEquals("\\documentclass{foo}\n\\usepackage{foo}[bar]\n\n\\begin{document}\n\n\\end{document}\n",
                document{
                    usepackage("foo", "bar")
                    documentClass("foo")
                })
    }

    @Test
    fun withContentTest() {
        assertEquals("\n\\begin{document}\n\n\\begin{left}\nfoo\n\\end{left}\n\n\\end{document}\n", document{left{+"foo"}})
        assertEquals("\n\\begin{document}\n\n\\begin{right}\nfoo\n\\end{right}\n\n\\end{document}\n", document{right{+"foo"}})
        assertEquals("\n\\begin{document}\n\n\\begin{center}\nfoo\n\\end{center}\n\n\\end{document}\n", document{center{+"foo"}})
        assertEquals("\n\\begin{document}\n\n\\begin{itemize}\n\n\\item a\n" +
                "\\item b\n\\item c\n\\end{itemize}\n\n\\end{document}\n",
                document{itemize{
                    item { +"a" }
                    item { +"b" }
                    item { +"c" }
                }})
        assertEquals("\n\\begin{document}\n\n\\begin{enumerate}\n\n\\item a\n" +
                "\\item b\n\\item c\n\\end{enumerate}\n\n\\end{document}\n",
                document{enumerate{
                    item { +"a" }
                    item { +"b" }
                    item { +"c" }
                }})
        assertEquals("\n\\begin{document}\n\n\\begin{math}\nfoo\n\\end{math}\n\n\\end{document}\n", document{math{+"foo"}})
    }

}