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

abstract class WikiClient(private val firstPageOnly: Boolean = false) {

    abstract val lang: String
    abstract val category: String

    private val formatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    private val jsonMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JodaModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun listRappers(): List<SearchResultEntry> {
        val url = "https://$lang.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=+incategory:$category"
        val searchResult = fetch(url)
        val result = mutableListOf<SearchResultEntry>()
        result += searchResult.query.search

        if (!firstPageOnly) {
            var offset = 10
            while (offset <= searchResult.query.searchinfo.totalhits) {
                val offsetUrl = "$url&sroffset=$offset"
                result += fetch(offsetUrl).query.search
                offset += 10
            }
        }

        return result
    }

    private fun fetch(url: String): SearchResult {
        val json = Unirest.get(url).asString().body!!
        println("fetching $url")
        return jsonMapper.readValue(json, SearchResult::class.java)
    }

    fun findDateOfBirth(title: String): LocalDate? {
        val url = "https://$lang.wikipedia.org/wiki/$title"
        println("fetching $url")

        return Jsoup
                .connect(url)
                .get()
                .getElementsByClass("bday")
                .filter { it.hasAttr("datetime") }
                .map { formatter.parseLocalDate(it.attr("datetime")) }
                .firstOrNull()
    }

}
