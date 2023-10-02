package search.flibusta.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import search.flibusta.Downloader
import search.flibusta.utils.Fb2Parser.sentencesOf

class Fb2ParserTest {

  @Test
  fun sentenceSequence() {
    val bookId = 253438
    val downloader = Downloader()
    val file = downloader.tryDownloadBook(bookId)!!
    val sentence = sentencesOf(file).first().toString()
    assertThat(sentence).isEqualTo("Антуан де Сент-Экзюпери")
  }
}