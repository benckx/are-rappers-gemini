package be.encelade.gemini.client

class EnglishWikiClient(firstPageOnly: Boolean = false) : WikiClient(firstPageOnly) {

    override val lang: String = "en"
    override val category: String = "American_rappers"

}
