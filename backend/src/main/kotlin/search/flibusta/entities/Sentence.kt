package search.flibusta.entities

import search.flibusta.utils.AnalyzersUtils.extendedMorphologicalBasis

class Sentence(private val wholeText: String) {

  private val basis = extendedMorphologicalBasis(wholeText)

  fun contains(basis: Set<String>): Boolean {
    return this.basis.containsAll(basis)
  }

  override fun toString(): String {
    return wholeText
  }
}