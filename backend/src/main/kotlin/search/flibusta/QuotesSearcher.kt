package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.entities.FlibustaBook
import search.flibusta.entities.QuotesCollection
import search.flibusta.utils.AnalyzersUtils.morphologicalBasis
import search.flibusta.utils.FictionBookUtils.sentencesOf
import java.lang.Thread.startVirtualThread
import java.util.concurrent.ConcurrentLinkedQueue

class QuotesSearcher(private val catalog: FlibustaRussianCatalog, private val downloader: FlibustaDownloader) {

  private val log = getLogger(javaClass)

  fun searchQuotesCollections(author: String, q: String): Collection<QuotesCollection> {
    val bibliography = catalog.bibliographyOf(author)
    val booksTotal = bibliography.sumOf { it.books.size }
    log.info("{} — \"{}\": {} books found", author, q, booksTotal)
    val basis = morphologicalBasis(q)
    val result = ConcurrentLinkedQueue<QuotesCollection>()
    val threads = mutableListOf<Thread>()
    for ((foundAuthor, books) in bibliography) {
      val thread = startVirtualThread {
        for (book in books) {
          val quotes = quotesOf(book, basis)
          if (quotes.isNotEmpty()) {
            result.add(QuotesCollection(foundAuthor, book, quotes))
          }
        }
      }
      threads.add(thread)
    }
    for (t in threads) {
      t.join()
    }
    return result
  }

  private fun quotesOf(book: FlibustaBook, q: Set<String>): Set<String> {
    return try {
      val file = downloader.downloadFb2(book.id)
      buildSet {
        for (sentence in sentencesOf(file)) {
          if (q in sentence) {
            add(sentence.toString())
          }
        }
      }
    } catch (e: RuntimeException) {
      log.warn("Can't download fb2 $book", e)
      emptySet()
    }
  }
}