package search.flibusta

import org.apache.commons.csv.CSVFormat.newFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.utils.FlibustaBook
import java.net.URL
import java.util.concurrent.atomic.AtomicReference
import java.util.zip.ZipInputStream

class Catalog(private val url: URL) {

  private companion object {
    const val LAST_NAME = "Last Name"
    const val FIRST_NAME = "First Name"
    const val MIDDLE_NAME = "Middle Name"
    const val TITLE = "Title"
    const val SUBTITLE = "Subtitle"
    const val LANGUAGE = "Language"
    const val YEAR = "Year"
    const val SERIES = "Series"
    const val ID = "ID"
  }

  private val csvFormat =
    newFormat(';').builder()
      .setHeader(LAST_NAME, FIRST_NAME, MIDDLE_NAME, TITLE, SUBTITLE, LANGUAGE, YEAR, SERIES, ID)
      .setSkipHeaderRecord(true)
      .build()

  private val authorsToBooks = AtomicReference<Map<String, Set<FlibustaBook>>>()

  init {
    updateCatalog()
  }

  fun updateCatalog() {
    val log = getLogger(javaClass)
    val authorsToBooksUpdate = mutableMapOf<String, MutableSet<FlibustaBook>>()
    log.info("Catalog update...")
    val urlStream = url.openStream()
    val bufferedStream = urlStream.buffered()
    val zipStream = ZipInputStream(bufferedStream)
    zipStream.use {
      val filename = zipStream.nextEntry?.name
      require(filename == "catalog.txt") {
        "Required 'catalog.txt', but found '$filename'"
      }
      val bufferedReader = zipStream.bufferedReader()
      val csvParser = csvFormat.parse(bufferedReader)
      csvParser.use {
        for (record in csvParser) {
          val author = parseAuthor(record)
          val book = parseBook(record)
          val collection = authorsToBooksUpdate.getOrPut(author, ::mutableSetOf)
          collection.add(book)
        }
      }
      zipStream.closeEntry()
    }
    authorsToBooks.set(authorsToBooksUpdate)
    log.info("Catalog successfully updated.")
  }


  private fun parseBook(record: CSVRecord): FlibustaBook {
    val title = record.get(TITLE)
    val subtitle = record.get(SUBTITLE)
    val id = record.get(ID).toLong()
    for (name in listOf(title, subtitle)) {
      if (name.isNotBlank()) {
        return FlibustaBook(id, name)
      }
    }
    val series = record.get(SERIES)
    val year = record.get(YEAR)
    val composedName = listOf(series, year).filter(String::isNotBlank).joinToString(" ")
    require(composedName.isNotBlank()) {
      "Can't parse $record"
    }
    return FlibustaBook(id, composedName)
  }

  private fun parseAuthor(record: CSVRecord): String {
    val lastName = record.get(LAST_NAME)
    val firstName = record.get(FIRST_NAME)
    val middleName = record.get(MIDDLE_NAME)
    return listOf(firstName, lastName, middleName).filter(String::isNotBlank).joinToString(" ")
  }

  fun searchBooksQuotes(author: String, words: List<String>): Map<String, List<String>> {
    TODO()
  }

  fun suggestSimilarAuthors(author: String): List<String> {
    TODO()
  }

  private fun searchQuotes(bookId: Long, words: List<String>): List<String> {
    TODO()
  }
}