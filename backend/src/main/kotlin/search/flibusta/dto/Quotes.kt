package search.flibusta.dto

data class Quotes(val quotes: Map<FlibustaBook, Set<String>>) : Search {

  override val containsQuotes = true
}