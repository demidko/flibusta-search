package search.flibusta.utils

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.tartarus.snowball.ext.RussianStemmer

/**
 * Предназначен для использования в одном потоке.
 */
@JvmInline
value class MorphAnalyzer(private val stemmer: RussianStemmer = RussianStemmer()) {

  /**
   * Морфологическая основа предложения обогащенная дополнительными стемами.
   * Максимально расширена всеми вариантами в одну кучу, чтобы было легче проверять [queryBases]
   */
  fun sentenceBasis(sentence: String): Set<String> {
    return buildSet {
      whileSplit(sentence) {
        add(stem(it))
        addAll(lemmas(it))
      }
    }
  }

  /**
   * Морфологические основы предложения (может быть много разных вариантов).
   * Каждый вариант максимально сужен, чтобы было больше шансов на вхождение в [sentenceBasis]
   */
  fun queryBases(sentence: String): Set<Set<String>> {
    var results = mutableSetOf<MutableSet<String>>(mutableSetOf())
    whileSplit(sentence) {
      val lemmas = lemmas(it)
      if (lemmas.isEmpty()) {
        results.add(stem(it))
      } else {
        if (lemmas.size == 1) {
          results.add(lemmas.first())
        } else {
          results = newMultiverse(results, lemmas)
        }
      }
    }
    return results
  }

  private fun newMultiverse(source: Set<Set<String>>, els: Set<String>): MutableSet<MutableSet<String>> {
    val multiverse = mutableSetOf<MutableSet<String>>()
    for (origin in source) {
      for (el in els) {
        val newVariant = origin.toMutableSet()
        newVariant.add(el)
        multiverse.add(newVariant)
      }
    }
    return multiverse
  }

  private fun MutableSet<MutableSet<String>>.add(el: String) {
    forEach {
      it.add(el)
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