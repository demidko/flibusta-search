package search.flibusta

import org.apache.commons.collections4.CollectionUtils.permutations
import org.apache.commons.csv.CSVFormat.newFormat
import org.apache.commons.csv.CSVRecord
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.dto.FlibustaBook
import search.flibusta.utils.StringUtils.levenshteinDistance
import search.flibusta.utils.StringUtils.normalizedName
import search.flibusta.utils.StringUtils.variants
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

  private val variantsToCanonicalNames = AtomicReference<Map<String, Set<String>>>()

  private val log = getLogger(javaClass)

  private var brokenRecords = 0
  private var recordsTotal = 0

  init {
    updateCatalog()
  }

  fun updateCatalog() {
    brokenRecords = 0
    recordsTotal = 0
    val authorsToBooksUpdate = mutableMapOf<String, MutableSet<FlibustaBook>>()
    log.info("Catalog update...")
    val urlStream = url.openStream()
    val bufferedStream = urlStream.buffered()
    val zipStream = ZipInputStream(bufferedStream)
    val variantsToCanonicalNamesUpdate = mutableMapOf<String, MutableSet<String>>()
    zipStream.use {
      val filename = zipStream.nextEntry?.name
      require(filename == "catalog.txt") {
        "Required catalog.txt, but found $filename"
      }
      val bufferedReader = zipStream.bufferedReader()
      val csvParser = csvFormat.parse(bufferedReader)
      csvParser.use {
        for (record in csvParser) {
          val lang = record.get(LANGUAGE)
          if (lang != "ru") {
            continue
          }
          ++recordsTotal
          val nameParts = parseAuthor(record)
          val canonicalName = nameParts.joinToString(" ")
          val nameVariants = downgrade(nameParts)
          for (v in nameVariants) {
            if(v.length < 5) {
              continue
            }
            val canonicalNames = variantsToCanonicalNamesUpdate.getOrPut(v, ::mutableSetOf)
            canonicalNames.add(canonicalName)
          }
          val book = parseBook(record)
          val booksCollection = authorsToBooksUpdate.getOrPut(canonicalName, ::mutableSetOf)
          booksCollection.add(book)
        }
      }
    }
    authorsToBooks.set(authorsToBooksUpdate)
    variantsToCanonicalNames.set(variantsToCanonicalNamesUpdate)
    log.info("Catalog successfully updated.")
    if (brokenRecords > 0) {
      log.warn("Books parsed: $recordsTotal. Broken CSV records detected: $brokenRecords")
    }
  }

  private fun parseBook(record: CSVRecord, useLastValueAsId: Boolean = false): FlibustaBook {
    val title = record.get(TITLE)
    val subtitle = record.get(SUBTITLE)
    val id =
      if (useLastValueAsId) {
        record.last().toInt()
      } else try {
        record.get(ID).toInt()
      } catch (e: RuntimeException) {
        ++brokenRecords
        return parseBook(record, true)
      }
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

  private fun parseAuthor(record: CSVRecord): List<String> {
    val lastName = record.get(LAST_NAME) ?: ""
    val firstName = record.get(FIRST_NAME) ?: ""
    val middleName = record.get(MIDDLE_NAME) ?: ""
    val fullName = listOf(firstName, lastName, middleName)
    return fullName.flatMap(::normalizedName)
  }

  fun bibliographySearch(author: String): Set<FlibustaBook> {
    val catalog = authorsToBooks.get()
    return catalog[author] ?: extendedBibliographySearch(author)
  }

  private fun extendedBibliographySearch(author: String): Set<FlibustaBook> {
    val catalog = authorsToBooks.get()
    val bibliography = mutableSetOf<FlibustaBook>()
    for (name in downgrade(author)) {
      catalog[name]?.let(bibliography::addAll)
    }
    if (bibliography.isEmpty()) {
      return moreExtendedBibliographySearch(author)
    }
    return bibliography
  }

  private fun moreExtendedBibliographySearch(author: String): Set<FlibustaBook> {
    val recognizedAuthor = fastRecognizeAuthor(author) ?: return emptySet()
    log.warn("\"$author\" recognized as \"$recognizedAuthor\"")
    return authorsToBooks.get()[recognizedAuthor] ?: emptySet()
  }

  private fun fastRecognizeAuthor(author: String): String? {
    val nameVariants = variants(author)
    val foundAuthors = mutableSetOf<String>()
    val variantsToAuthors = variantsToCanonicalNames.get()
    for (v in nameVariants) {
      val newAuthors = variantsToAuthors[v] ?: continue
      foundAuthors.addAll(newAuthors)
    }
    if (foundAuthors.isEmpty()) {
      return null
    }
    var closestDistance = Int.MAX_VALUE
    var closestAuthor = ""
    for (otherAuthor in foundAuthors) {
      val distance = levenshteinDistance(author, otherAuthor)
      if (distance < closestDistance) {
        closestDistance = distance
        closestAuthor = otherAuthor
      }
    }
    return closestAuthor
  }

  private fun downgrade(name: String): Set<String> {
    return downgrade(normalizedName(name))
  }

  private fun downgrade(name: List<String>): Set<String> {
    val result = mutableSetOf<String>()
    for (p in permutations(name)) {
      for (i in 1..p.size) {
        val variant = p.take(i).joinToString(" ")
        result.add(variant)
      }
    }
    return result
  }

  /**
   * Все известные имена авторов (перестановкам & сокращениям соответствуют канонические имена)
   */
  fun authors(): Map<String, Set<String>> {
    return variantsToCanonicalNames.get()
  }
}