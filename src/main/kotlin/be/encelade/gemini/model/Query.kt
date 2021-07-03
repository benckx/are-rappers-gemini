package be.encelade.gemini.model

data class Query(val search: List<SearchResultEntry>,
                 val searchinfo: SearchInfo)
