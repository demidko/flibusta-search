package search.flibusta.utils

import org.apache.commons.collections4.CollectionUtils.permutations

typealias NormalizedName = Set<String>

object TextUtils {

  fun normalizedName(n: String): List<String> {
    val parts = mutableListOf<String>()
    whileSplit(normalizedWord(n), parts::add)
    return parts
  }

  fun normalizedWord(w: String): String {
    return w.lowercase().replace('ё', 'е')
  }

  inline fun whileSplit(sentence: String, crossinline f: (String) -> Unit) {
    val buf = StringBuilder()
    for (char in sentence) {
      if (char.isLetter()) {
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
