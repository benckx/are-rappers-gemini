package be.encelade.gemini

import be.encelade.gemini.model.Zodiac
import org.joda.time.LocalDate

object ZodiacTranslator {

    fun findSign(date: LocalDate): Zodiac {
        val sameYear = date.withYear(2021)
        val nextYear = date.withYear(2022)

        return Zodiac.values().find { zodiac ->
            (zodiac.start == date || zodiac.start.isBefore(sameYear)) && zodiac.end.isAfter(sameYear) ||
            (zodiac.start == date || zodiac.start.isBefore(nextYear)) && zodiac.end.isAfter(nextYear)
        }!!
    }

}
