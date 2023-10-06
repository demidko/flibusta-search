package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import search.flibusta.utils.MultiName

object FlibustaRussianCatalogTest {

  private val log = LoggerFactory.getLogger(javaClass)

  val catalog = FlibustaRussianCatalog("https://flibusta.is")

  @Test
  fun testSearchAuthor() {
    val authors = catalog.searchAuthors("""Кира Райли~""") // ищем Кору Рейли
    log.info("${authors.size} authors found")
    for ((name, books) in authors) {
      log.info(name)
      for (b in books) {
        log.info("  $b")
      }
    }
    //assertThat(authors).hasSize(9)
  }
}