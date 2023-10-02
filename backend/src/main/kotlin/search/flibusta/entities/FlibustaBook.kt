package search.flibusta.entities

data class FlibustaBook(val id: Int, val name: String) {

  override fun toString(): String {
    return "book id $id: $name"
  }
}
