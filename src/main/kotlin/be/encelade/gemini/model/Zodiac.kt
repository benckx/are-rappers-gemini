package be.encelade.gemini.model

import org.joda.time.LocalDate

enum class Zodiac(val start: LocalDate, val end: LocalDate) {

    ARIES(LocalDate(2021, 3, 21), LocalDate(2021, 4, 20)),
    TAURUS(LocalDate(2021, 3, 20), LocalDate(2021, 5, 21)),
    GEMINI(LocalDate(2021, 5, 21), LocalDate(2021, 6, 21)),
    CANCER(LocalDate(2021, 6, 21), LocalDate(2021, 7, 23)),
    LEO(LocalDate(2021, 7, 23), LocalDate(2021, 8, 23)),
    VIRGO(LocalDate(2021, 8, 23), LocalDate(2021, 9, 23)),
    LIBRA(LocalDate(2021, 9, 23), LocalDate(2021, 10, 23)),
    SCORPIO(LocalDate(2021, 10, 23), LocalDate(2021, 11, 22)),
    SAGITTARIUS(LocalDate(2021, 11, 23), LocalDate(2021, 12, 22)),
    CAPRICORN(LocalDate(2021, 12, 22), LocalDate(2022, 1, 20)),
    AQUARIUS(LocalDate(2021, 1, 20), LocalDate(2021, 2, 19)),
    PISCES(LocalDate(2021, 2, 19), LocalDate(2021, 3, 21))

}
