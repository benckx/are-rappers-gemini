package be.encelade.gemini.client

import be.encelade.gemini.model.SearchResult
import be.encelade.gemini.model.SearchResultEntry
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mashape.unirest.http.Unirest
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup


class WikiClient {

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    private val jsonMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JodaModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun listFrenchRappers(): List<SearchResultEntry> {
        val url = "https://fr.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=+incategory:Rappeur_fran%C3%A7ais"
        val json = Unirest.get(url).asString().body!!
        return jsonMapper.readValue(json, SearchResult::class.java).query.search
    }

    fun findDateOfBirthFrench(title: String): LocalDate? {
        return Jsoup
                .connect("https://fr.wikipedia.org/wiki/$title")
                .get()
                .getElementsByClass("bday")
                .filter { it.hasAttr("datetime") }
                .map { formatter.parseLocalDate(it.attr("datetime")) }
                .firstOrNull()
    }

}
