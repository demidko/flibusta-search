package search.flibusta.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import search.flibusta.FlibustaDownloader
import search.flibusta.utils.FictionBookUtils.sentencesOf


object FictionBookUtilsTest {

  @Test
  fun testSentenceSequence() {
    val downloader = FlibustaDownloader("https://flibusta.is")
    val file = downloader.holdFb2(253438)
    val analyzer = MorphAnalyzer()
    val sentence = sentencesOf(file, analyzer).first().toString()
    assertThat(sentence).isEqualTo("Антуан де Сент-Экзюпери")
  }
}