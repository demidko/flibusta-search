package search.flibusta.entities

data class QuotesCollection(val author: String, val book: FlibustaBook, val quotes: Set<String>)