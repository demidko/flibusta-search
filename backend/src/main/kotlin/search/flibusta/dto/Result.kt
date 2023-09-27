package search.flibusta.dto

data class Result(val booksToQuotes: Map<String, List<String>>) : Search {

  override val containsQuotes = true
}