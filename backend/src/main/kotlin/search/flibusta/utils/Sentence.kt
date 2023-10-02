package search.flibusta.utils

import com.github.demidko.aot.WordformMeaning

class Sentence(private val text: String, private val lemmas: Set<WordformMeaning>, private val stems: Set<String>) {

  fun contains(quote: QuoteQuery): Boolean {
    return lemmas.containsAll(quote.lemmas) && stems.containsAll(quote.stems)
  }

  override fun toString(): String {
    return text
  }
}