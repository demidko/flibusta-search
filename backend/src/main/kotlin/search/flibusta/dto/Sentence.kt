package search.flibusta.dto

class Sentence(private val wholeText: String, private val basis: Set<String>) {

  operator fun contains(queryBases: Set<Set<String>>): Boolean {
    return queryBases.any {
      basis.containsAll(it)
    }
  }

  override fun toString(): String {
    return wholeText
  }
}