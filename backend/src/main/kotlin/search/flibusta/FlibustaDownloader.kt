package search.flibusta

import org.slf4j.LoggerFactory.getLogger
import search.flibusta.utils.UrlUtils.urlOf
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.ConcurrentSkipListSet

class FlibustaDownloader(private val mirror: String) {

  private val cacheDirectory = File("cache")

  private val log = getLogger(javaClass)

  private val activeFiles = ConcurrentSkipListSet<File>()

  private val minimalFreeSpaceBytes = 750 * 1024 * 1024

  init {
    if (cacheDirectory.exists()) {
      require(cacheDirectory.isDirectory)
    } else {
      check(cacheDirectory.mkdir())
    }
  }

  fun holdFb2(id: Int): File {
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
    activeFiles.add(file)
    return file
  }

  fun releaseFb2(file: File) {
    activeFiles.remove(file)
  }

  inline fun useFb2(id: Int, crossinline func: (File) -> Unit) {
    val fb2 = holdFb2(id)
    try {
      func(fb2)
    } finally {
      releaseFb2(fb2)
    }
  }

  tailrec fun clearCache() {
    if (cacheDirectory.freeSpace < minimalFreeSpaceBytes) {
      log.warn("Cache cleanup...")
      var freedUp = 0L
      val (trashBooks, _) = cacheDirectory.listFiles()!!.toList().chunked(2)
      for (book in trashBooks) {
        if (book in activeFiles) {
          continue
        }
        require(book.delete())
        freedUp += book.length()
      }
      log.warn("{} MB freed up", freedUp.toDouble() / 1024 / 1024)
      return clearCache()
    }
  }
}