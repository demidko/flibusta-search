package search.flibusta

import search.flibusta.utils.StringUtils.levenshteinDistance
import search.flibusta.utils.StringUtils.variants
import kotlin.collections.Map.Entry

class NamesSearcher(private val catalog: Catalog) {

  private class RatedCanonSet(val distance: Int, val canons: Set<String>)

  fun similarNames(name: String): Set<String> {
    return catalog.authors().asSequence()
      .map(ratedCanonSet(variants(name)))
      .sortedBy(RatedCanonSet::distance)
      .take(10)
      .flatMap(RatedCanonSet::canons)
      .toSet()
  }

  private fun ratedCanonSet(variants: Set<String>): (Entry<String, Set<String>>) -> RatedCanonSet {
    return { (otherVariant, otherCanon) ->
      ratedCanonSet(variants, otherVariant, otherCanon)
    }
  }

  private fun ratedCanonSet(nameVariants: Set<String>, canonVariant: String, canonSet: Set<String>): RatedCanonSet {
    return nameVariants.asSequence().map(ratedCanonSet(canonVariant, canonSet)).minBy(RatedCanonSet::distance)
  }

  private fun ratedCanonSet(canonVariant: String, canonSet: Set<String>): (String) -> RatedCanonSet {
    return { ratedCanonSet(it, canonVariant, canonSet) }
  }

  private fun ratedCanonSet(nameVariant: String, canonVariant: String, canonSet: Set<String>): RatedCanonSet {
    val distance = levenshteinDistance(nameVariant, canonVariant)
    return RatedCanonSet(distance, canonSet)
  }
}