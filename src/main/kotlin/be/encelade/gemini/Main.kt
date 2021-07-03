package be.encelade.gemini

import be.encelade.gemini.client.WikiClient

fun main() {
    val client = WikiClient()

    client
            .listFrenchRappers()
            .forEach { entry ->
                println(entry.title)
                client.findDateOfBirthFrench(entry.title)?.let { dateOfBirth ->
                    println("$dateOfBirth / ${ZodiacTranslator.findSign(dateOfBirth)}")
                }
            }

}
