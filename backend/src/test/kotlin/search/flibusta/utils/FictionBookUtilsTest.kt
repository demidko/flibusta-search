package search.flibusta.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import search.flibusta.FlibustaDownloader
import search.flibusta.utils.FictionBookUtils.sentencesOf


class FictionBookUtilsTest {

  @Test
  fun sentenceSequence() {
    val downloader = FlibustaDownloader("https://flibusta.is")
    val file = downloader.downloadFb2(253438)
    val sentence = sentencesOf(file).first().toString()
    assertThat(sentence).isEqualTo("Антуан де Сент-Экзюпери")
  }
}