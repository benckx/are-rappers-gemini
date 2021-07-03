package be.encelade.gemini

import be.encelade.gemini.ZodiacTranslator.calculateZodiacSign
import be.encelade.gemini.client.FrenchWikiClient

fun main() {
    val client = FrenchWikiClient()

    client
            .listFrenchRappers()
            .forEach { entry ->
                println(entry.title)
                client.findDateOfBirthFrench(entry.title)?.let { dateOfBirth ->
                    println("$dateOfBirth / ${calculateZodiacSign(dateOfBirth)}")
                }
            }

}
