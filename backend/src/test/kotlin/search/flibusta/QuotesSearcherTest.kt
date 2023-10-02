package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class QuotesSearcherTest {

  @Test
  fun searchQuotes() {
    val mirror = "https://flibusta.is"
    val catalog = FlibustaRussianCatalog(mirror)
    val downloader = FlibustaDownloader(mirror)
    val searcher = QuotesSearcher(catalog, downloader)
    // ошибка (буква у) допущена намерено, поиск должен распознать автора
    val collections = searcher.searchQuotesCollections("экзупери", "Принц сказал розе")
    assertThat(collections).hasSize(9)
  }
}