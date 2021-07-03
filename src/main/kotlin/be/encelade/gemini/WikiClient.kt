package be.encelade.gemini

import be.encelade.gemini.model.SearchResult
import be.encelade.gemini.model.SearchResultEntry
import com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mashape.unirest.http.Unirest

class WikiClient {

    private val jsonMapper = ObjectMapper()
            .registerModule(KotlinModule())
            .registerModule(JodaModule())
            .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun listFrenchRappers(): List<SearchResultEntry> {
        val url = "https://fr.wikipedia.org/w/api.php?action=query&list=search&format=json&srsearch=+incategory:Rappeur_fran%C3%A7ais"
        val json = Unirest.get(url).asString().body!!
        return jsonMapper.readValue(json, SearchResult::class.java).query.search
    }

}
