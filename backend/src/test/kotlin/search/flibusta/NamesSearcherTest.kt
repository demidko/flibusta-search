package search.flibusta

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import search.flibusta.BooksCatalogTest.flibustaCatalog

object NamesSearcherTest {

  @Test
  @Disabled
  fun similarNames() {
    val catalog = flibustaCatalog()
    val namesSearcher = NamesSearcher(catalog)
    val similarNames = namesSearcher.similarNames("Экзюпери")
    println(similarNames)
  }
}