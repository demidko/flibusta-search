package search.flibusta.dto

data class Result(val booksToQuotes: Map<String, Set<String>>) : Search {

  override val containsQuotes = true
}