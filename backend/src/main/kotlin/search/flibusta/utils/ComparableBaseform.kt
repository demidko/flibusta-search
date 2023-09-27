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
      return meaning == other
    }
  }

  private class StringWrapper(private val s: String) : ComparableBaseform {

    override fun hashCode(): Int {
      return s.hashCode()
    }

    override fun equals(other: Any?): Boolean {
      return s == other
    }
  }

  companion object {

    fun lookupForBaseforms(word: String): Set<ComparableBaseform> {
      val meanings = lookupForMeanings(word)
      if (meanings.isEmpty()) {
        return setOf(StringWrapper(word))
      }
      return meanings.map(WordformMeaning::getLemma).map(::MeaningWrapper).toSet()
    }
  }
}
