package search.flibusta.dto

data class Suggest(val authors: Set<String>) : Search {

  override val containsQuotes = false
}