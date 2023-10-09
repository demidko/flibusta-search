package search.flibusta.utils

import com.fasterxml.jackson.databind.ObjectMapper
import search.flibusta.dto.FlibustaBook
import search.flibusta.dto.Sentence
import java.io.BufferedWriter
import java.io.Writer

class ResponseStreamer(private val writer: Writer, private val mapper: ObjectMapper) {

  fun logProcessing(author: String, book: FlibustaBook) {
    writer.write("$author — ${book.title}\n")
    writer.flush()
  }

  fun addQuote(author: String, book: FlibustaBook, sentence: Sentence) {
    val quote = sentence.toString()
    val aggregate = listOf(author, book.id, book.title, quote)
    val json = mapper.writeValueAsString(aggregate)
    writer.write("$json\n")
    writer.flush()
  }
}