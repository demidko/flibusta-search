package search.flibusta.entities

class Sentence(private val wholeText: String, private val basis: Set<String>) {

  operator fun contains(basis: Set<String>): Boolean {
    return this.basis.containsAll(basis)
  }

  override fun toString(): String {
    return wholeText
  }
}