package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class DownloaderTest {

  @Test
  fun downloadBookTest() {
    val downloader = Downloader()
    val file = downloader.tryDownloadBook(226654)!!
    assertThat(file.exists())
    assertThat(file.length() > 100)
  }
}