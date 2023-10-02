package search.flibusta.utils

import org.apache.commons.collections4.CollectionUtils.permutations
import org.apache.commons.text.similarity.LevenshteinDistance

object StringUtils {

  val levenshteinDistance = LevenshteinDistance()::apply

  private val vowels = "аяуюоеэиы".toSet()

  fun variants(name: String): Set<String> {
    return normalizedName(name)
      .let(::permutations)
      .map { it.joinToString(" ") }
      .toSet()
  }

  fun normalizedName(n: String): List<String> {
    val parts = mutableListOf<String>()
    whileSplit(normalizedWordOf(n), parts::add)
    return parts
  }

  fun normalizedWordOf(w: String): String {
    return w.lowercase().replace('ё', 'е')
  }

  inline fun whileSplit(sentence: String, crossinline f: (String) -> Unit) {
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

  private fun isVowel(char: Char): Boolean {
    return char in vowels
  }

  fun simpleStemOf(word: String): String {
    return normalizedWordOf(word).trimEnd(::isVowel)
  }
}
