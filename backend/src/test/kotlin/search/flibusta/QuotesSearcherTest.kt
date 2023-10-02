package search.flibusta

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import search.flibusta.BooksCatalogTest.flibustaCatalog

class QuotesSearcherTest {

  /**
   * Пожалуй, самый главный тест в системе, проверяющий, что свободный поиск отрабатывает как и задумывалось
   */
  @Test
  @Disabled
  fun similarQuotesTest() {
    val downloader = Downloader()
    val catalog = flibustaCatalog()
    val quotesSearcher = QuotesSearcher(catalog, downloader)
    val quotes = quotesSearcher.similarQuotes("Экзюпери", "Принц роза")
    println(quotes)
  }
}