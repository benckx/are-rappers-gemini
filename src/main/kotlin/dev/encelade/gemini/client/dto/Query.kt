package dev.encelade.gemini.client.dto

data class Query(
    val search: List<SearchResultEntry>,
    val searchinfo: SearchInfo
)
