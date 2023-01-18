package dev.encelade.gemini

import dev.encelade.gemini.client.EnglishWikiClient
import dev.encelade.gemini.client.FrenchWikiClient
import dev.encelade.gemini.services.Zodiac
import dev.encelade.gemini.services.ZodiacCalculator.calculateZodiacSign
import org.apache.commons.io.FileUtils.writeStringToFile
import java.io.File

fun main() {

    // TODO: add more categories: https://en.wikipedia.org/wiki/Category:American_male_rappers (list of tuples <lang, category>)
    val clientFr = FrenchWikiClient()
    val clientEn = EnglishWikiClient()

    val rows = mutableListOf<String>()
    val zodiacCounter = mutableMapOf<Zodiac, Int>()

    listOf(clientFr, clientEn)
        .forEach { client ->
            client
                .searchInCategory()
                .forEach { entry ->
                    try {
                        client.findDateOfBirth(entry.title)?.let { dateOfBirth ->
                            val zodiacSign = calculateZodiacSign(dateOfBirth)
                            val columns = listOf(entry.title, dateOfBirth.toString(), zodiacSign.formatted())
                            rows += columns.joinToString(separator = ";")
                            zodiacCounter[zodiacSign] = zodiacCounter.getOrDefault(zodiacSign, 0) + 1
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
