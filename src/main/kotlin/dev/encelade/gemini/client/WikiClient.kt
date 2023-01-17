package dev.encelade.gemini.client

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mashape.unirest.http.Unirest
import dev.encelade.gemini.client.dto.SearchResult
import dev.encelade.gemini.client.dto.SearchResultEntry
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class WikiClient(private val firstPageOnly: Boolean = false) {

    abstract val lang: String
    abstract val category: String

    private val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    private val jsonMapper =
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JodaModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun searchInCategory(): List<SearchResultEntry> {
        val url =
            "https://$lang.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=+incategory:$category"
        val searchResult = fetch(url)
        val result = mutableListOf<SearchResultEntry>()
        result += searchResult.query.search

        if (!firstPageOnly) {
            var offset = 10
            while (offset <= searchResult.query.searchinfo.totalhits) {
                val nextPageUrl = "$url&sroffset=$offset"
                result += fetch(nextPageUrl).query.search
                offset += 10
            }
        }

        return result.sortedBy { it.title }
    }

    private fun fetch(url: String): SearchResult {
        println("fetching $url")
        val json = Unirest.get(url).asString().body!!
        return jsonMapper.readValue(json, SearchResult::class.java)
    }

    fun findDateOfBirth(title: String): LocalDate? {
        val url = "https://$lang.wikipedia.org/wiki/$title"
        println("fetching $url")

        val document = Jsoup.connect(url).get()
        return localize1(document) ?: localize2(document)
    }

    // works on French Wiki
    private fun localize1(document: Document): LocalDate? {
        return document
            .getElementsByClass("bday")
            .filter { it.hasAttr("datetime") }
            .map { dateFormatter.parseLocalDate(it.attr("datetime")) }
            .firstOrNull()
    }

    // works on English Wiki
    private fun localize2(document: Document): LocalDate? {
        return document
            .getElementsByClass("bday")
            .firstNotNullOfOrNull {
                try {
                    dateFormatter.parseLocalDate(it.text())
                } catch (t: Throwable) {
                    null
                }
            }
    }

}
