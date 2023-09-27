package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class CachedDownloaderTest {

  @Test
  fun downloadBookTest() {
    val downloader = CachedDownloader()
    val file = downloader.downloadBook(226654)
    assertThat(file.exists())
    assertThat(file.length() > 100)
  }
}