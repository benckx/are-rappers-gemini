package dev.encelade.gemini.client

import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import dev.encelade.gemini.client.dto.SearchResult
import dev.encelade.gemini.client.dto.SearchResultEntry
import kong.unirest.Unirest
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class WikiClient(private val lang: String = "en", private val firstPageOnly: Boolean = false) {

    private val jsonMapper = JsonMapper
        .builder()
        .addModule(KotlinModule.Builder().build())
        .addModule(JodaModule())
        .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
        .build()

    fun search(category: String): List<SearchResultEntry> {
        val firstPage = searchByOffset(category)
        val totalEntries = firstPage.query.searchinfo.totalhits
        val entries = mutableListOf<SearchResultEntry>()
        entries += firstPage.query.search

        if (!firstPageOnly) {
            var offset = 10
            while (offset <= totalEntries) {
                entries += searchByOffset(category, offset).query.search
                offset += 10
            }
        }

        return entries.sortedBy { entry -> entry.title }
    }

    // "https://$lang.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=+incategory:$category"
    private fun searchByOffset(category: String, offset: Int? = null): SearchResult {
        var query = Unirest
            .get("https://$lang.wikipedia.org/w/api.php")
            .queryString("action", "query")
            .queryString("list", "search")
            .queryString("format", "json")
            .queryString("srsearch", "incategory:$category")

        if (offset != null) {
            query = query.queryString("sroffset", offset.toString())
        }

        val json = query.asString().body!!
        println("json: $json")
        return jsonMapper.readValue(json, SearchResult::class.java)
    }

    fun findDateOfBirth(title: String): LocalDate? {
        val url = "https://$lang.wikipedia.org/wiki/$title"
        val document = Jsoup.connect(url).get()
        return localizeEn(document) ?: localizeFr(document)
    }

    private companion object {

        private val dateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")

        // works on French Wiki
        fun localizeFr(document: Document): LocalDate? {
            return document
                .getElementsByClass("bday")
                .flatMap { it.allElements }
                .filter { it.hasAttr("datetime") }
                .map { dateFormatter.parseLocalDate(it.attr("datetime")) }
                .firstOrNull()
        }

        // works on English Wiki
         fun localizeEn(document: Document): LocalDate? {
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

}
