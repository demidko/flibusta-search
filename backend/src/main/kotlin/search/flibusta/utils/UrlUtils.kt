package search.flibusta.utils

import java.net.URI
import java.net.URL

object UrlUtils {

  fun urlOf(uri: String): URL {
    return URI(uri).toURL()
  }
}