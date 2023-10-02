package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import search.flibusta.dto.FlibustaBook
import java.net.URI

object BooksCatalogTest {

  @Test
  @Disabled
  fun testCatalog() {
    val catalog = flibustaCatalog()
    val bibliography = catalog.bibliographySearch("Лао-цзы").map(FlibustaBook::name)
    assertThat(bibliography).contains("Дао Дэ-цзин")
  }

  fun flibustaCatalog(): Catalog {
    val uri = URI("https://flibusta.is/catalog/catalog.zip")
    val url = uri.toURL()
    return Catalog(url)
  }
}