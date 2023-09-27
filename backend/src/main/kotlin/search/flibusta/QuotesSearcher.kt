package search.flibusta

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.FictionBookReader
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class QuotesSearcher(private val catalog: Catalog, private val downloader: Downloader) {

  private val logger = getLogger(javaClass)

  fun searchQuotes(author: String, query: String): Map<FlibustaBook, Set<String>> {
    val bibliography = catalog.bibliography(author)
    val lemmas =
      query.split(" ")
        .filter(String::isNotBlank)
        .flatMap(::lookupForMeanings)
        .map(WordformMeaning::getLemma)
        .toSet()
    return buildMap {
      for (meta in bibliography) {
        val (id, name) = meta
        val bookName = "$author — $name"
        logger.info("Downloading $bookName...")
        val book = downloader.downloadBook(id)
        logger.info("Search \"$query\" in $bookName...")
        val quotes = searchQuotes(book, lemmas)
        put(meta, quotes)
      }
    }
  }

  private fun searchQuotes(book: File, lemmas: Set<WordformMeaning>): Set<String> {
    val reader = FictionBookReader(DataInputStream(BufferedInputStream(FileInputStream(book))))
    reader.use {

    }
  }

  private fun readLemmas(sentence: String): Set<WordformMeaning> {
    return split(sentence)
      .flatMap(::lookupForMeanings)
      .map(WordformMeaning::getLemma)
      .toSet()
  }

  private fun split(sentence: String): Sequence<String> {
    val buf = StringBuilder()
    return sequence {
      for (char in sentence) {
        if (char.isLetter()) {
          buf.append(char)
          continue
        }
        if(char == '-') {
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