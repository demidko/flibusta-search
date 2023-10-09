package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.FictionBookUtils.sentencesOf
import search.flibusta.utils.MorphAnalyzer
import search.flibusta.utils.ResponseStreamer
import java.lang.Thread.startVirtualThread

class QuotesSearcher(private val catalog: FlibustaRussianCatalog, private val downloader: FlibustaDownloader) {

  private val log = getLogger(javaClass)

  fun searchQuotes(author: String, query: String, results: ResponseStreamer) {
    val bibliographies = catalog.searchAuthors(author)
    val booksTotal = bibliographies.sumOf { it.books.size }
    log.info("{} — \"{}\": found {} books for further search", author, query, booksTotal)
    val basis = MorphAnalyzer().queryBases(query)
    val threads = ArrayList<Thread>(booksTotal)
    for ((foundAuthor, books) in bibliographies) {
      for (book in books) {
        results.logProcessing(foundAuthor, book)
        threads.add(startVirtualThread {
          searchQuotes(foundAuthor, book, basis, results)
        })
      }
    }
    threads.forEach(Thread::join)
  }

  private fun searchQuotes(author: String, book: FlibustaBook, query: Set<Set<String>>, results: ResponseStreamer) {
    val analyzer = MorphAnalyzer()
    try {
      downloader.useFb2(book.id) {
        for (sentence in sentencesOf(it, analyzer)) {
          if (query in sentence) {
            results.addQuote(author, book, sentence)
          }
        }
      }
    } catch (e: RuntimeException) {
      log.warn("Problem with $book", e)
    }
  }
}