package dev.encelade.gemini.client.dto

import org.joda.time.DateTime

data class SearchResultEntry(
    val title: String,
    val pageid: Int,
    val snippet: String,
    val timestamp: DateTime
)
