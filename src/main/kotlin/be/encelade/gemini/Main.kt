package be.encelade.gemini

import be.encelade.gemini.client.WikiClient

fun main() {
    val client = WikiClient()

    client
            .listFrenchRappers()
            .forEach { entry ->
                println(entry)
                println(client.findDateOfBirthFrench(entry.title))
            }

}
