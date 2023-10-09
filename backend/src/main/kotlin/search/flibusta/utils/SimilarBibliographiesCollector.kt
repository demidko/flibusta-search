package search.flibusta.utils

import search.flibusta.dto.Bibliography
import search.flibusta.dto.FlibustaBook

class SimilarBibliographiesCollector(requiredAuthor: String, private val maxLevenshteinDistance: Int) {

  private val cachingNamesCalculator = CachingNamesCalculator(requiredAuthor)

  private val authorToBibliography = mutableMapOf<String, MutableList<FlibustaBook>>()

  fun processBook(similarAuthor: String, id: Int, title: String) {
    if (distance(similarAuthor) > maxLevenshteinDistance) {
      return
    }
    val flibustaBook = FlibustaBook(id, title)
    val bibliography = authorToBibliography.getOrPut(similarAuthor, ::mutableListOf)
    bibliography.add(flibustaBook)
  }

  fun listBibliographies(): List<Bibliography> {
    val result = authorToBibliography.map { (author, bibliography) ->
      Bibliography(author, bibliography)
    }
    return result.sortedBy { (author, _) -> distance(author) }
  }

  private fun distance(similarAuthor: String): Int {
    return cachingNamesCalculator.calculateDistance(similarAuthor)
  }
}