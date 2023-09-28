package search.flibusta

import org.apache.commons.collections4.CollectionUtils.permutations
import org.apache.commons.text.similarity.LevenshteinDistance
import search.flibusta.utils.TextUtils.normalizedName
import kotlin.collections.Map.Entry

class NamesSearcher(private val catalog: Catalog) {

  private class RatedName(val name: String, val distance: Int)

  private val distance = LevenshteinDistance()

  fun similarNames(name: String): Set<String> {
    return catalog.authors().asSequence()
      .map(rate(variants(name)))
      .sortedBy(RatedName::distance)
      .map(RatedName::name)
      .take(10)
      .toSet()
  }

  private fun variants(name: String): Set<String> {
    return normalizedName(name)
      .let(::permutations)
      .map { it.joinToString(" ") }
      .toSet()
  }

  private fun rate(source: Set<String>): (Entry<String, Set<String>>) -> RatedName {
    return { (canonicalName, nameVariants) ->
      RatedName(canonicalName, distance(source, nameVariants))
    }
  }

  private fun distance(source: Set<String>, other: Set<String>): Int {
    var resultDistance = Int.MAX_VALUE
    for (originalName in source) {
      for (possibleName in other) {
        val newDistance = distance.apply(possibleName, originalName)
        if (newDistance < resultDistance) {
          resultDistance = newDistance
        }
      }
    }
    return resultDistance
  }
}