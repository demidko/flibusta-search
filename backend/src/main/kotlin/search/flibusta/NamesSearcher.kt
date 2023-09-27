package search.flibusta

import search.flibusta.utils.NameUtils.possibleNames

class NamesSearcher(private val catalog: Catalog) {

  fun similarNames(author: String): Set<String> {
    val names = possibleNames(author)
    TODO()
  }
}