package be.encelade.gemini

fun main() {
    val client = WikiClient()
    client.listFrenchRappers().forEach { println(it) }
}
