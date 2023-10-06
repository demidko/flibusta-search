package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.entities.FlibustaBook
import search.flibusta.entities.QuotesCollection
import search.flibusta.utils.FictionBookUtils.sentencesOf
import search.flibusta.utils.MorphAnalyzer
import java.lang.Thread.startVirtualThread
import java.util.concurrent.ConcurrentLinkedQueue

class QuotesSearcher(private val catalog: FlibustaRussianCatalog, private val downloader: FlibustaDownloader) {

  private val log = getLogger(javaClass)

  fun quotes(author: String, query: String): Collection<QuotesCollection> {
    val bibliography = catalog.searchAuthors(author)
    val booksTotal = bibliography.sumOf { it.books.size }
    log.info("{} — \"{}\": found {} books for further search", author, query, booksTotal)
    val queryBasis = MorphAnalyzer().morphologicalBasis(query)
    val quotesCollections = ConcurrentLinkedQueue<QuotesCollection>()
    val threads = ArrayList<Thread>(booksTotal)
    for ((foundAuthor, books) in bibliography) {
      for (book in books) {
        threads.add(startVirtualThread {
          val quotes = quotesOf(book, queryBasis)
          if (quotes.isNotEmpty()) {
            quotesCollections.add(QuotesCollection(foundAuthor, book, quotes))
          }
        })
      }
    }
    threads.forEach(Thread::join)
    return quotesCollections
  }

  private fun quotesOf(book: FlibustaBook, q: Set<String>): Set<String> {
    val result = mutableSetOf<String>()
    val analyzer = MorphAnalyzer()
    try {
      downloader.useFb2(book.id) {
        for (sentence in sentencesOf(it, analyzer)) {
          if (q in sentence) {
            result.add(sentence.toString())
          }
        }
      }
    } catch (e: RuntimeException) {
      log.warn("Problem with $book", e)
    }
    return result
  }
}