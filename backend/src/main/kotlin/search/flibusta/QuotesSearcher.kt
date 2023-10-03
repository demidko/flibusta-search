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

  private val analyzer = MorphAnalyzer()

  fun searchQuotesCollections(author: String, q: String): Collection<QuotesCollection> {
    val bibliography = catalog.bibliographyOf(author)
    val booksTotal = bibliography.sumOf { it.books.size }
    log.info("{} — \"{}\": {} books found", author, q, booksTotal)
    val basis = analyzer.morphologicalBasis(q)
    val result = ConcurrentLinkedQueue<QuotesCollection>()
    val threads = mutableListOf<Thread>()
    for ((foundAuthor, books) in bibliography) {
      val thread = startVirtualThread {
        val threadAnalyzer = MorphAnalyzer()
        for (book in books) {
          val quotes = quotesOf(book, basis, threadAnalyzer)
          if (quotes.isNotEmpty()) {
            result.add(QuotesCollection(foundAuthor, book, quotes))
          }
        }
      }
      threads.add(thread)
    }
    threads.forEach(Thread::join)
    return result
  }

  private fun quotesOf(book: FlibustaBook, q: Set<String>, analyzer: MorphAnalyzer): Set<String> {
    val file =
      try {
        downloader.downloadFb2(book.id)
      } catch (e: RuntimeException) {
        log.warn("Can't download $book", e)
        return emptySet()
      }
    return buildSet {
      for (sentence in sentencesOf(file, analyzer)) {
        if (q in sentence) {
          add(sentence.toString())
        }
      }
    }
  }
}