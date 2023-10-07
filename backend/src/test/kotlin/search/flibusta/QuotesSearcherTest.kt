package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import search.flibusta.FlibustaRussianCatalogTest.catalog
import search.flibusta.utils.FictionBookUtilsTest.downloader

class QuotesSearcherTest {

  private val searcher by lazy { QuotesSearcher(catalog, downloader) }

  @Test
  fun searchQuotes() {
    // ошибка (буква у) допущена намерено, поиск должен распознать автора
    val collections = searcher.quotes("экзюпери", "Принц сказал розе")
    assertThat(collections).hasSize(9)
  }
}