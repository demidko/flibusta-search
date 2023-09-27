package search.flibusta.utils

import com.google.common.collect.Collections2.permutations

object NameUtils {

  fun possibleNames(author: String): Set<String> {
    val permutations =
      author.split(" ")
        .filter(String::isNotBlank)
        .let(::permutations)
    return buildSet {
      for (p in permutations) {
        for (size in 1..p.size) {
          p.take(size)
            .joinToString(" ")
            .let(::add)
        }
      }
    }
  }
}
