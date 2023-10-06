package search.flibusta.entities

data class FlibustaBook(val id: Int, val title: String) {

  override fun toString(): String {
    return "book id $id: $title"
  }
}
