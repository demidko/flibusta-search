package search.flibusta.utils

import org.apache.commons.collections4.CollectionUtils.permutations
import org.apache.commons.text.similarity.LevenshteinDistance
import java.util.Comparator.reverseOrder
import kotlin.Int.Companion.MAX_VALUE

class MultiName(source: String) {

  companion object {

    val levenshteinDistance = LevenshteinDistance()
  }

  private val categorizedVariants = normalizedVariantsByCategory(source)

  private val longCategoriesFirst = categorizedVariants.keys.toSortedSet(reverseOrder())

  private val uncategorizedVariants = categorizedVariants.values.flatten().toSet()

  fun calculateDistance(other: MultiName): Int {
    val sameCategory = bestCategory(other) ?: return bestDistance(uncategorizedVariants, other.uncategorizedVariants)
    val variants = categorizedVariants[sameCategory]!!
    val otherVariants = other.categorizedVariants[sameCategory]!!
    return bestDistance(variants, otherVariants)
  }

  private fun bestCategory(other: MultiName): Int? {
    return other.longCategoriesFirst.firstOrNull { it in longCategoriesFirst }
  }

  private fun bestDistance(variants: Set<String>, otherVariants: Set<String>): Int {
    var bestDistance = MAX_VALUE
    for (variant in variants) {
      for (otherVariant in otherVariants) {
        val newDistance = levenshteinDistance.apply(variant, otherVariant)
        if (newDistance == 0) {
          return 0
        }
        if (newDistance < bestDistance) {
          bestDistance = newDistance
        }
      }
    }
    return bestDistance
  }

  private fun normalizedVariantsByCategory(name: String): Map<Int, Set<String>> {
    val parts = name.lowercase().replace('ё', 'е').split(" ", "-")
    val permutations = mutableMapOf<Int, MutableSet<String>>()
    for (permutedParts in permutations(parts)) {
      for (i in 1..permutedParts.size) {
        val permutation = permutedParts.take(i)
        val length = permutation.size
        val collection = permutations.getOrPut(length, ::mutableSetOf)
        collection.add(permutation.joinToString(" "))
      }
    }
    return permutations
  }

  override fun toString(): String {
    return uncategorizedVariants.toString()
  }

  override fun hashCode(): Int {
    return uncategorizedVariants.hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other is MultiName) {
      return uncategorizedVariants == other.uncategorizedVariants
    }
    return false
  }
}