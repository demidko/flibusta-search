package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import search.flibusta.dto.FlibustaBook
import java.net.URI

class BooksCatalogTest {

  @Test
  fun testCatalog() {
    val uri = URI("https://flibusta.is/catalog/catalog.zip")
    val url = uri.toURL()
    val catalog = Catalog(url)
    val bibliography = catalog.bibliography("Лао-цзы").map(FlibustaBook::name)
    assertThat(bibliography).contains("Дао Дэ-цзин")
  }
}