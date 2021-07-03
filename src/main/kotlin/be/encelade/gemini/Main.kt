package be.encelade.gemini

import be.encelade.gemini.ZodiacTranslator.calculateZodiacSign
import be.encelade.gemini.client.EnglishWikiClient
import be.encelade.gemini.client.FrenchWikiClient
import be.encelade.gemini.model.Zodiac

fun main() {
    val clientFr = FrenchWikiClient()
    val clientEn = EnglishWikiClient()

    val rows = mutableListOf<String>()
    val allEntries = mutableMapOf<Zodiac, Int>()

    listOf(clientFr, clientEn)
            .forEach { client ->
                client
                        .listRappers()
                        .forEach { entry ->
                            try {
                                client.findDateOfBirth(entry.title)?.let { dateOfBirth ->
                                    val zodiacSign = calculateZodiacSign(dateOfBirth)
                                    rows += listOf(entry.title, dateOfBirth.toString(), zodiacSign.formatted()).joinToString(separator = ";")

                                    allEntries.computeIfAbsent(zodiacSign) { 0 }
                                    allEntries[zodiacSign] = allEntries[zodiacSign]!! + 1
                                }
                            } catch (t: Throwable) {
                                println(t)
                            }
                        }
            }


    rows.forEach { row -> println(row) }

    println()
    println()
    val total = allEntries.values.sum().toFloat()
    allEntries.forEach { (zodiacSign, value) ->
        println("${zodiacSign.formatted()} -> ${value / total}")
    }

}
