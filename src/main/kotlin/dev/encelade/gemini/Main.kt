package dev.encelade.gemini

import dev.encelade.gemini.client.WikiClient
import dev.encelade.gemini.services.Zodiac
import dev.encelade.gemini.services.ZodiacCalculator.calculateZodiacSign
import org.apache.commons.io.FileUtils.writeStringToFile
import java.io.File

// TODO: separate case csv exists vs. don't exist?
fun main() {

    val client = WikiClient()
    val categories = listOf("French_rappers")//, "American_male_rappers", "American_women_rappers")
    val entries = categories
        .flatMap { category -> WikiClient().searchInCategory(category) }
        .distinctBy { entry -> entry.pageid }

    println("found ${entries.size} entries")

    val csvRows = mutableListOf<String>()
    val zodiacCounter = mutableMapOf<Zodiac, Int>()

    entries
        .forEach { entry ->
            try {
                client.findDateOfBirth(entry.title)?.let { dateOfBirth ->
                    val zodiacSign = calculateZodiacSign(dateOfBirth)
                    println("${entry.title} -> $dateOfBirth (${zodiacSign.formatted()})")
                    val columns = listOf(entry.title, dateOfBirth.toString(), zodiacSign.formatted())
                    csvRows += columns.joinToString(separator = ";")
                    zodiacCounter[zodiacSign] = zodiacCounter.getOrDefault(zodiacSign, 0) + 1
                }
            } catch (t: Throwable) {
                println(t)
            }
        }

    csvRows.forEach { row -> println(row) }
    writeStringToFile(File("rappers.csv"), csvRows.joinToString(separator = "\n"), "UTF-8")

    println()
    println()

    val total = zodiacCounter.values.sum().toFloat()
    Zodiac.values().forEach { zodiac ->
        val frequency = zodiacCounter[zodiac]!! / total
        println("${zodiac.formatted()} -> ${frequency * 100} %")
    }

}
