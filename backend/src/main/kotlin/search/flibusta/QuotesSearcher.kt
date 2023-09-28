package search.flibusta

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.FictionBookReader
import search.flibusta.utils.TextUtils.normalizedWord
import search.flibusta.utils.TextUtils.whileSplit
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class QuotesSearcher(private val catalog: Catalog, private val downloader: Downloader) {

  private val logger = getLogger(javaClass)

  private val vowels = "аяуюоеэиы".toSet()

  fun similarQuotes(author: String, query: String): Map<FlibustaBook, Set<String>> {
    val bibliography = catalog.bibliography(author)
    val (queryLemmas, queryStems) = lemmasOf(query)
    return buildMap {
      for (meta in bibliography) {
        val (id, name) = meta
        val bookName = "$author — $name"
        logger.info("Downloading $bookName...")
        val book = downloader.downloadBook(id)
        logger.info("Search \"$query\" in $bookName...")
        val quotes = similarQuotes(book, queryLemmas, queryStems)
        logger.info("For \"$query\" found ${quotes.size} quotes in $bookName")
        put(meta, quotes)
      }
    }
  }

  private fun similarQuotes(book: File, queryLemmas: Set<WordformMeaning>, queryStems: Set<String>): Set<String> {
    val quotes = mutableSetOf<String>()
    val reader = FictionBookReader(DataInputStream(BufferedInputStream(FileInputStream(book))))
    reader.use {
      for (sentence in reader.sentenceSequence()) {
        val (sentenceLemmas, sentenceStems) = lemmasAndStemsTogether(sentence)
        if (sentenceLemmas.containsAll(queryLemmas) && sentenceStems.containsAll(queryStems)) {
          quotes.add(sentence)
        }
      }
    }
    return quotes
  }

  private fun isVowel(char: Char): Boolean {
    return char in vowels
  }

  private fun simpleStem(word: String): String {
    return normalizedWord(word).trimEnd(::isVowel)
  }

  /**
   * Ищутся как леммы, так и стеммы для каждого слова в любом случае
   */
  private fun lemmasAndStemsTogether(sentence: String): Pair<Set<WordformMeaning>, Set<String>> {
    val lemmasCollection = mutableSetOf<WordformMeaning>()
    val stemsCollection = mutableSetOf<String>()
    whileSplit(sentence) {
      lookupForMeanings(it).map(WordformMeaning::getLemma).let(lemmasCollection::addAll)
      stemsCollection.add(simpleStem(it))
    }
    return lemmasCollection to stemsCollection
  }

  /**
   * В первую очередь ищутся леммы, а для слов, где лемма не обнаружена, возвращается стем
   */
  private fun lemmasOf(sentence: String): Pair<Set<WordformMeaning>, Set<String>> {
    val lemmasCollection = mutableSetOf<WordformMeaning>()
    val stemsCollection = mutableSetOf<String>()
    whileSplit(sentence) {
      val lemmas = lookupForMeanings(it)
      if (lemmas.isEmpty()) {
        stemsCollection.add(simpleStem(it))
      } else {
        lemmas.map(WordformMeaning::getLemma).let(lemmasCollection::addAll)
      }
    }
    return lemmasCollection to stemsCollection
  }
}