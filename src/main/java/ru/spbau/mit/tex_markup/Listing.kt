package ru.spbau.mit.tex_markup

class Listing(override val name: String): TexTag() {
    override val container: TextMode = TextMode()

    private fun TextMode.createItem() {
        +"\n\\item "
    }

    fun item(init: TextMode.() -> Unit) {
        container.createItem()
        container.init()
    }
}

fun TextMode.enumerate(init: Listing.() -> Unit) {
    val listing = Listing("enumerate")
    listing.init()
    +listing.build()
}

fun TextMode.itemize(init: Listing.() -> Unit) {
    val listing = Listing("itemize")
    listing.init()
    +listing.build()
}