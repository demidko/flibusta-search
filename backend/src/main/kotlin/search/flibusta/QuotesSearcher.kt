package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import java.io.File

class QuotesSearcher(private val catalog: Catalog, private val downloader: Downloader) {

  private val logger = getLogger(javaClass)

  fun searchQuotes(author: String, query: String): Map<FlibustaBook, Set<String>> {
    val bibliography = catalog.bibliography(author)
    val words = query.split(" ").filter(String::isNotBlank).toSet()
    return buildMap {
      for (meta in bibliography) {
        val (id, name) = meta
        logger.info("Downloading $author — $name...")
        val book = downloader.downloadBook(id)
        logger.info("$author — $name downloaded as '$book'")
        val quotes = searchQuotes(book, words)
        put(meta, quotes)
      }
    }
  }

  private fun searchQuotes(book: File, words: Set<String>): Set<String> {
    TODO()
  }
}