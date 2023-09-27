package search.flibusta.utils

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings

sealed interface ComparableBaseform {

  abstract override fun hashCode(): Int

  abstract override fun equals(other: Any?): Boolean

  private class MeaningWrapper(private val meaning: WordformMeaning) : ComparableBaseform {

    override fun hashCode(): Int {
      return meaning.hashCode()
    }

    override fun equals(other: Any?): Boolean {
      if (other is MeaningWrapper) {
        return meaning == other.meaning
      }
      return meaning == other
    }
  }

  private class StringWrapper(private val stem: String) : ComparableBaseform {

    override fun hashCode(): Int {
      return stem.hashCode()
    }

    override fun equals(other: Any?): Boolean {
      if (other is StringWrapper) {
        return stem == other.stem
      }
      return stem == other
    }
  }

  companion object {

    private val vowels = "аяуюоеёэиы".toSet()

    private fun isVowel(char: Char): Boolean {
      return char in vowels
    }

    fun lookupForBaseforms(word: String): Set<ComparableBaseform> {
      val meanings = lookupForMeanings(word)
      if (meanings.isEmpty()) {
        val stem = word.trimEnd(::isVowel)
        return setOf(StringWrapper(stem))
      }
      return meanings.map(WordformMeaning::getLemma).map(::MeaningWrapper).toSet()
    }
  }
}
