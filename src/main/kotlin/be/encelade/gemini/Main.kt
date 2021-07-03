package be.encelade.gemini

import be.encelade.gemini.ZodiacTranslator.calculateZodiacSign
import be.encelade.gemini.client.EnglishWikiClient
import be.encelade.gemini.client.FrenchWikiClient
import org.apache.commons.lang3.StringUtils

fun main() {
    val rows = mutableListOf<String>()
    val clientFr = FrenchWikiClient(true)
    val clientEn = EnglishWikiClient(false)

    listOf(clientEn)
            .forEach { client ->
                client
                        .listRappers()
                        .forEach { entry ->
                            try {
                                client.findDateOfBirth(entry.title)?.let { dateOfBirth ->
                                    val zodiacSign = StringUtils.capitalize(calculateZodiacSign(dateOfBirth).name.lowercase())
                                    rows += listOf<String>(entry.title, dateOfBirth.toString(), zodiacSign).joinToString(separator = ";")
                                }
                            } catch (t: Throwable) {
                                println(t)
                            }
                        }
            }


    rows.forEach { println(it) }

}
