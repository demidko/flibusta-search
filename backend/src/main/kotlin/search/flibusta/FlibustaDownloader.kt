package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.utils.UrlUtils.urlOf
import java.io.File
import java.io.FileOutputStream

class FlibustaDownloader(private val mirror: String) {

  private val cacheDirectory = File("cache")

  private val log = getLogger(javaClass)

  init {
    if (cacheDirectory.exists()) {
      require(cacheDirectory.isDirectory)
    } else {
      check(cacheDirectory.mkdir())
    }
  }

  /**
   * Может возникнуть [RuntimeException], например,
   * если на флибусте будет отсутствовать fb2-файл (бывает что присутствует лишь pdf)
   */
  fun downloadFb2(id: Int): File {
    val file = cacheDirectory.resolve("$id.fb2.zip")
    if (file.exists()) {
      return file
    }
    val metaUrl = urlOf("$mirror/b/$id/fb2")
    val metaConnection = metaUrl.openConnection()
    val locationHeader = metaConnection.getHeaderField("location")
    check(locationHeader.isNotBlank())
    val locationUrl = urlOf(locationHeader)
    val fileBytes = locationUrl.readBytes()
    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.use {
      fileOutputStream.write(fileBytes)
    }
    return file
  }
}