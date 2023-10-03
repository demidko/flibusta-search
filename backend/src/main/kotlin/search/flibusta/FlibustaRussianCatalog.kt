package search.flibusta

import org.apache.commons.csv.CSVFormat.newFormat
import org.apache.commons.csv.CSVRecord
import org.apache.lucene.analysis.ru.RussianAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field.Store.YES
import org.apache.lucene.document.IntField
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.DirectoryReader.open
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.FuzzyQuery
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.store.ByteBuffersDirectory
import org.apache.lucene.store.Directory
import org.slf4j.LoggerFactory.getLogger
import search.flibusta.entities.Bibliography
import search.flibusta.entities.FlibustaBook
import search.flibusta.utils.UrlUtils.urlOf
import java.util.concurrent.atomic.AtomicReference
import java.util.zip.ZipInputStream
import kotlin.Int.Companion.MAX_VALUE

class FlibustaRussianCatalog(mirror: String) {

  private val catalogUrl = urlOf("$mirror/catalog/catalog.zip")

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

  private val log = getLogger(javaClass)

  private var brokenRecords = 0

  private var recordsTotal = 0

  private val index = AtomicReference<Directory>()

  private val analyzer = RussianAnalyzer()

  private val config = IndexWriterConfig(analyzer)

  private val queryParser = QueryParser("author", analyzer)

  init {
    updateCatalog()
  }

  fun bibliographyOf(author: String): List<Bibliography> {
    val q = queryForAuthor(author)
    val index = index.get()
    val reader = open(index)
    val authorToBibliography = mutableMapOf<String, MutableList<FlibustaBook>>()
    reader.use {
      val searcher = IndexSearcher(reader)
      val hits = searcher.search(q, MAX_VALUE)
      val storedFields = searcher.storedFields()
      for (hit in hits.scoreDocs) {
        val document = storedFields.document(hit.doc)
        val foundAuthor = document.getField("author").stringValue()
        val book = document.getField("book").stringValue()
        val id = document.getField("id").numericValue().toInt()
        val collection = authorToBibliography.getOrPut(foundAuthor, ::mutableListOf)
        collection.add(FlibustaBook(id, book))
      }
    }
    return authorToBibliography.map { (author, books) -> Bibliography(author, books) }
  }

  fun updateCatalog() {
    logUpdateStarted()
    val urlStream = catalogUrl.openStream()
    val bufferedStream = urlStream.buffered()
    val zipStream = ZipInputStream(bufferedStream)
    val newIndex = ByteBuffersDirectory()
    val writer = IndexWriter(newIndex, config)
    try {
      requireCatalog(zipStream)
      val bufferedReader = zipStream.bufferedReader()
      val csvParser = csvFormat.parse(bufferedReader)
      csvParser.use {
        for (record in csvParser) {
          if (isRussian(record)) {
            writer.addDocument(documentOf(record))
            incrementRecordsCounter()
          }
        }
      }
      index.set(newIndex)
      logSuccessfullyUpdate()
    } finally {
      zipStream.close()
      writer.close()
    }
  }

  private fun queryForAuthor(author: String): Query {
    // todo надо уменьшить допустимое расстояние (много нерелевантых рещультатов)
    return queryParser.parse("$author~0.7")
  }

  private fun documentOf(record: CSVRecord): Document {
    return Document().apply {
      add(authorOf(record))
      add(bookOf(record))
      add(idOf(record))
    }
  }

  private fun incrementRecordsCounter() {
    ++recordsTotal
  }

  private fun isRussian(record: CSVRecord): Boolean {
    return record.get(LANGUAGE) == "ru"
  }

  private fun logUpdateStarted() {
    brokenRecords = 0
    recordsTotal = 0
    log.info("Catalog update...")
  }

  private fun incrementBrokenRecordsCounter() {
    ++brokenRecords
  }

  private fun logSuccessfullyUpdate() {
    log.info("Catalog successfully updated.")
    if (brokenRecords > 0) {
      log.warn("Books parsed: $recordsTotal. Broken CSV records detected: $brokenRecords")
    }
  }

  private fun requireCatalog(stream: ZipInputStream) {
    val entry = stream.nextEntry
    requireNotNull(entry)
    val filename = entry.name
    require(filename == "catalog.txt") { "Required catalog.txt, but found $filename" }
  }

  private fun idOf(record: CSVRecord): IntField {
    val id = try {
      record.get(ID).toInt()
    } catch (e: RuntimeException) {
      incrementBrokenRecordsCounter()
      record.last().toInt()
    }
    return IntField("id", id, YES)
  }

  private fun bookOf(record: CSVRecord): StringField {
    val title = record.get(TITLE) ?: ""
    val subtitle = record.get(SUBTITLE) ?: ""
    val series = record.get(SERIES) ?: ""
    val year = record.get(YEAR) ?: ""
    val fullName = listOf(title, subtitle, series, year).filter(String::isNotBlank).joinToString(" - ")
    return StringField("book", fullName, YES)
  }

  private fun authorOf(record: CSVRecord): TextField {
    val lastName = record.get(LAST_NAME) ?: ""
    val firstName = record.get(FIRST_NAME) ?: ""
    val middleName = record.get(MIDDLE_NAME) ?: ""
    val fullName = listOf(firstName, lastName, middleName).filter(String::isNotBlank).joinToString(" ")
    return TextField("author", fullName, YES)
  }
}