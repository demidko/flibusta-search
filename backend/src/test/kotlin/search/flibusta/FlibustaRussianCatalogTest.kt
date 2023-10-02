package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class FlibustaRussianCatalogTest {

  @Test
  fun bibliographyOf() {
    val catalog = FlibustaRussianCatalog("https://flibusta.is")
    val bibliography = catalog.bibliographyOf("экзупери")
    assertThat(bibliography).hasSize(2)
  }
}