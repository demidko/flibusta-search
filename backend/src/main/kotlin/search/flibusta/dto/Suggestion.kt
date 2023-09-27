package search.flibusta.dto

data class Suggestion(val possibleAuthors: List<String>) : Search {

  override val containsQuotes = false
}