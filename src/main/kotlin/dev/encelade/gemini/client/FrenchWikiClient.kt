package dev.encelade.gemini.client

class FrenchWikiClient(firstPageOnly: Boolean = false) : WikiClient(firstPageOnly) {

    override val lang: String = "fr"
    override val category: String = "Rappeur_fran%C3%A7ais"

}
