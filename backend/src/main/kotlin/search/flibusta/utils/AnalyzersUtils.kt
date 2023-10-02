package search.flibusta.utils

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.tartarus.snowball.ext.RussianStemmer

object AnalyzersUtils {

  private val stemmer = RussianStemmer()

  /**
   * Морфологическая основа предложения обогащенная дополнительными стемами.
   */
  fun extendedMorphologicalBasis(sentence: String): Set<String> {
    return buildSet {
      whileSplit(sentence) {
        add(stem(it))
        addAll(lemmas(it))
      }
    }
  }

  /**
   * Морфологическая основа предложения.
   */
  fun morphologicalBasis(sentence: String): Set<String> {
    return buildSet {
      whileSplit(sentence) {
        val lemmas = lemmas(it)
        if (lemmas.isEmpty()) {
          add(stem(it))
        } else {
          addAll(lemmas(it))
        }
      }
    }
  }

  private fun lemmas(w: String): Set<String> {
    return lookupForMeanings(w)
      .map(WordformMeaning::getLemma)
      .map(WordformMeaning::toString)
      .toSet()
  }

  private fun stem(w: String): String {
    stemmer.current = w.lowercase().replace('ё', 'е')
    stemmer.stem()
    return stemmer.current
  }

  private inline fun whileSplit(sentence: String, crossinline f: (String) -> Unit) {
    val buf = StringBuilder()
    for (char in sentence) {
      if (char.isLetterOrDigit()) {
        buf.append(char)
        continue
      }
      if (buf.isEmpty()) {
        continue
      }
      f(buf.toString())
      buf.clear()
    }
    if (buf.isNotEmpty()) {
      f(buf.toString())
    }
  }
}