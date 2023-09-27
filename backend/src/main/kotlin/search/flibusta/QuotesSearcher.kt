package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.ComparableBaseform
import search.flibusta.utils.ComparableBaseform.Companion.lookupForBaseforms
import search.flibusta.utils.FictionBookReader
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class QuotesSearcher(private val catalog: Catalog, private val downloader: Downloader) {

  private val logger = getLogger(javaClass)

  fun searchQuotes(author: String, query: String): Map<FlibustaBook, Set<String>> {
    val bibliography = catalog.bibliography(author)
    val queryForms = baseformsOf(query)
    return buildMap {
      for (meta in bibliography) {
        val (id, name) = meta
        val bookName = "$author — $name"
        logger.info("Downloading $bookName...")
        val book = downloader.downloadBook(id)
        logger.info("Search \"$query\" in $bookName...")
        val quotes = searchQuotes(book, queryForms)
        logger.info("For \"$query\" found ${quotes.size} quotes in $bookName")
        put(meta, quotes)
      }
    }
  }

  private fun searchQuotes(book: File, queryForms: Set<ComparableBaseform>): Set<String> {
    val reader = FictionBookReader(DataInputStream(BufferedInputStream(FileInputStream(book))))
    reader.use {
      return buildSet {
        for (sentence in reader.sentenceSequence()) {
          val baseforms = baseformsOf(sentence)
          if (baseforms.containsAll(queryForms)) {
            add(sentence)
          }
        }
      }
    }
  }

  private fun baseformsOf(sentence: String): Set<ComparableBaseform> {
    return split(sentence).flatMap(::lookupForBaseforms).toSet()
  }

  private fun split(sentence: String): Sequence<String> {
    val buf = StringBuilder()
    return sequence {
      for (char in sentence) {
        if (char.isLetter()) {
          buf.append(char)
          continue
        }
        if (buf.isEmpty()) {
          continue
        }
        yield(buf.toString())
        buf.clear()
      }
      if (buf.isNotEmpty()) {
        yield(buf.toString())
      }
    }
  }
}