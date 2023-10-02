package search.flibusta

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.StringUtils.normalizedWordOf
import search.flibusta.utils.StringUtils.whileSplit
import java.io.File

class QuotesSearcher(private val catalog: Catalog, private val downloader: Downloader) {

  private val log = getLogger(javaClass)

  private val vowels = "аяуюоеэиы".toSet()

  fun similarQuotes(author: String, query: String): Map<FlibustaBook, Set<String>> {
    val bibliography = catalog.bibliographySearch(author)
    val (queryLemmas, queryStems) = lemmasOf(query)
    return buildMap {
      for (meta in bibliography) {
        val (id, name) = meta
        val bookName = "$author — $name"
        log.info("Downloading $bookName...")
        val book = downloader.tryDownloadBook(id) ?: continue
        log.info("Search \"$query\" in $bookName...")

        val quotes = similarQuotes(book, queryLemmas, queryStems)
        if (quotes.isNotEmpty()) {
          log.info("For \"$query\" found ${quotes.size} quotes in $bookName")
          put(meta, quotes)
        }

      }
    }
  }

  private fun similarQuotes(book: File, queryLemmas: Set<WordformMeaning>, queryStems: Set<String>): Set<String> {
    try {
      val quotes = mutableSetOf<String>()
      TODO()
      return quotes
    } catch (e: RuntimeException) {
      log.warn(e.message)
      return emptySet()
    }
  }

  private fun isVowel(char: Char): Boolean {
    return char in vowels
  }

  private fun simpleStem(word: String): String {
    return normalizedWordOf(word).trimEnd(::isVowel)
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