package be.encelade.gemini

import be.encelade.gemini.ZodiacTranslator.calculateZodiacSign
import be.encelade.gemini.client.EnglishWikiClient
import be.encelade.gemini.client.FrenchWikiClient
import be.encelade.gemini.model.Zodiac
import org.apache.commons.io.FileUtils.writeStringToFile
import java.io.File

fun main() {
    val clientFr = FrenchWikiClient()
    val clientEn = EnglishWikiClient()

    val rows = mutableListOf<String>()
    val zodiacCounter = mutableMapOf<Zodiac, Int>()

    listOf(clientFr, clientEn)
            .forEach { client ->
                client
                        .listRappers()
                        .forEach { entry ->
                            try {
                                client.findDateOfBirth(entry.title)?.let { dateOfBirth ->
                                    val zodiacSign = calculateZodiacSign(dateOfBirth)
                                    rows += listOf(entry.title, dateOfBirth.toString(), zodiacSign.formatted()).joinToString(separator = ";")

                                    zodiacCounter.computeIfAbsent(zodiacSign) { 0 }
                                    zodiacCounter[zodiacSign] = zodiacCounter[zodiacSign]!! + 1
                                }
                            } catch (t: Throwable) {
                                println(t)
                            }
                        }
            }


    rows.forEach { row -> println(row) }
    writeStringToFile(File("rappers.csv"), rows.joinToString(separator = "\n"), "UTF-8")

    println()
    println()

    val total = zodiacCounter.values.sum().toFloat()

    Zodiac.values().forEach { zodiac ->
        val frequency = zodiacCounter[zodiac]!! / total
        println("${zodiac.formatted()} -> $frequency")
    }

}
