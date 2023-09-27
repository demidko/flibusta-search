package search.flibusta

import org.junit.jupiter.api.Test
import java.net.URI

class BooksCatalogTest {

  @Test
  fun testCatalog() {
    val uri = URI("https://flibusta.is/catalog/catalog.zip")
    val url = uri.toURL()
    val catalog = Catalog(url)
  }
}