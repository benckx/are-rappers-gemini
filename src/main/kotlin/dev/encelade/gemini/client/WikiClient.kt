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

class WikiClient(private val lang: String = "en", private val firstPageOnly: Boolean = false) {

    private val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

    // TODO: use builder
    private val jsonMapper =
        ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JodaModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun searchInCategory(category: String): List<SearchResultEntry> {
        // TODO: build url
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

        return result.sortedBy { entry -> entry.title }
    }

    private fun fetch(url: String): SearchResult {
        println("fetching $url")
        val json = Unirest.get(url).asString().body!!
        return jsonMapper.readValue(json, SearchResult::class.java)
    }

    fun findDateOfBirth(title: String): LocalDate? {
        val url = "https://$lang.wikipedia.org/wiki/$title"
        val document = Jsoup.connect(url).get()
        return localizeEn(document) ?: localizeFr(document)
    }

    // works on French Wiki
    private fun localizeFr(document: Document): LocalDate? {
        return document
            .getElementsByClass("bday")
            .flatMap { it.allElements }
            .filter { it.hasAttr("datetime") }
            .map { dateFormatter.parseLocalDate(it.attr("datetime")) }
            .firstOrNull()
    }

    // works on English Wiki
    private fun localizeEn(document: Document): LocalDate? {
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
