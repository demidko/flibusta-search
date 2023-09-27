package search.flibusta

import java.io.File
import java.io.FileOutputStream
import java.net.URI

class Downloader {

  private val cacheDirectory = File("cache")

  init {
    if (cacheDirectory.exists()) {
      require(cacheDirectory.isDirectory)
    } else {
      check(cacheDirectory.mkdir())
    }
  }

  fun downloadBook(id: Long): File {
    val file = cacheDirectory.resolve("$id.fb2.zip")
    if (file.exists()) {
      return file
    }
    val metaUri = URI("https://flibusta.is/b/$id/fb2")
    val metaUrl = metaUri.toURL()
    val metaConnection = metaUrl.openConnection()
    val locationHeader = metaConnection.getHeaderField("location")
    check(locationHeader.isNotBlank())
    val locationUri = URI(locationHeader)
    val locationUrl = locationUri.toURL()
    val fileBytes = locationUrl.readBytes()
    val fileOutputStream = FileOutputStream(file)
    fileOutputStream.use {
      fileOutputStream.write(fileBytes)
    }
    return file
  }
}