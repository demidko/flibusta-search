package search.flibusta

import com.google.common.collect.Collections2
import com.google.common.truth.Truth.assertThat
import org.apache.commons.collections4.CollectionUtils
import org.junit.jupiter.api.Test

class DownloaderTest {

  @Test
  fun downloadBookTest() {
    val downloader = Downloader()
    val file = downloader.downloadBook(226654)
    assertThat(file.exists())
    assertThat(file.length() > 100)
  }

  @Test
  fun f() {
    val name = listOf("Daniel", "S", "Demidko")
    val perm = CollectionUtils.permutations(name)
    for(p in perm) {
      println(p)
    }
  }
}