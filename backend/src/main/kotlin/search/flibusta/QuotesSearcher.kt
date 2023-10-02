package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.entities.FlibustaBook
import search.flibusta.entities.QuotesCollection
import search.flibusta.utils.AnalyzersUtils.morphologicalBasis
import search.flibusta.utils.FictionBookUtils.sentencesOf

class QuotesSearcher(private val catalog: FlibustaRussianCatalog, private val downloader: FlibustaDownloader) {

  private val log = getLogger(javaClass)

  fun searchQuotesCollections(requiredAuthor: String, query: String): Set<QuotesCollection> {
    val bibliography = catalog.bibliographyOf(requiredAuthor)
    val basis = morphologicalBasis(query)
    return buildSet {
      for ((foundAuthor, books) in bibliography) {
        for (book in books) {
          val quotes = quotesOf(book, basis)
          if (quotes.isNotEmpty()) {
            add(QuotesCollection(foundAuthor, book, quotes))
          }
        }
      }
    }
  }

  private fun quotesOf(book: FlibustaBook, q: Set<String>): Set<String> {
    logDownloading(book)
    return try {
      val file = downloader.downloadFb2(book.id)
      buildSet {
        for (sentence in sentencesOf(file)) {
          if (sentence.contains(q)) {
            add(sentence.toString())
          }
        }
      }
    } catch (e: RuntimeException) {
      logDownloadingFailure(book, e)
      emptySet()
    }
  }

  private fun logDownloading(book: FlibustaBook) {
    log.info("Downloading $book...")
  }

  private fun logDownloadingFailure(book: FlibustaBook, e: RuntimeException) {
    log.warn("Can't download fb2 $book", e)
  }
}