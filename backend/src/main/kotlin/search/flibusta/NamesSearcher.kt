package search.flibusta

import org.apache.commons.text.similarity.LevenshteinDistance
import search.flibusta.utils.TextUtils.possibleNames

class NamesSearcher(private val catalog: Catalog) {

  private class RatedName(val name: String, val distance: Int)

  private val distance = LevenshteinDistance()

  fun similarNames(name: String): Set<String> {
    val variants = possibleNames(name)
    return catalog.authors().asSequence()
      .map(rate(variants))
      .sortedBy(RatedName::distance)
      .map(RatedName::name)
      .take(10)
      .toSet()
  }

  private fun rate(source: Set<String>): (String) -> RatedName {
    return {
      rate(source, it)
    }
  }

  private fun rate(source: Set<String>, other: String): RatedName {
    val distance = source.minOf {
      distance.apply(other, it)
    }
    return RatedName(other, distance)
  }
}