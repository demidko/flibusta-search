package search.flibusta.dto

data class Suggestion(val possibleAuthors: Set<String>) : Search {

  override val containsQuotes = false
}