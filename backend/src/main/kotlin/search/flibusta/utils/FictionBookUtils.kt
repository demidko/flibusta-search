package search.flibusta.utils

import org.slf4j.LoggerFactory
import org.w3c.dom.Node
import search.flibusta.entities.Sentence
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

object FictionBookUtils {

  private val factory = DocumentBuilderFactory.newInstance()

  private val sentenceStopSymbols = setOf('?', '!', '.')

  private val log = LoggerFactory.getLogger(javaClass)

  fun sentencesOf(fb2: File): Sequence<Sentence> {
    val zip = ZipInputStream(BufferedInputStream(FileInputStream(fb2)))
    zip.use {
      return sequence {
        var entry = zip.nextEntry
        while (entry != null) {
          val filename = entry.name
          if(isFb2(filename)) {
            val document = factory.newDocumentBuilder().parse(zip)
            val nodes = document.getElementsByTagName("body")
            require(nodes.length > 0) { "$fb2: body not found" }
            for (i in 0..<nodes.length) {
              val body = nodes.item(i)
              require(body.hasChildNodes()) { "$fb2 body: content not found" }
              yieldAll(sentencesOf(body))
            }
          } else {
            log.warn("$fb2: expected fb2, but found $filename")
          }
          entry = zip.nextEntry
        }
      }
    }
  }

  fun isFb2(filename: String): Boolean {
    return filename.endsWith(".fb2") || filename.endsWith(".fbd")
  }

  private fun sentencesOf(node: Node): Sequence<Sentence> {
    if (!node.hasChildNodes() || node.nodeName == "p") {
      return sentencesOf(node.textContent)
    }
    val children = node.childNodes
    val count = children.length
    return sequence {
      for (i in 0..<count) {
        val child = children.item(i)
        yieldAll(sentencesOf(child))
      }
    }
  }

  private fun sentencesOf(text: String): Sequence<Sentence> {
    return sequence {
      val buf = StringBuilder()
      var isOpenQuote = false
      var wasSpace = false
      for (c in text) {
        if (c == '«') {
          isOpenQuote = true
        }
        if (c == '»') {
          isOpenQuote = false
        }
        if (isOpenQuote) {
          buf.append(c)
          continue
        }
        if (isWhitespace(c)) {
          if (wasSpace) {
            continue
          }
          buf.append(' ')
          wasSpace = true
          continue
        } else {
          wasSpace = false
        }
        if (isSentenceStopSymbol(c)) {
          if (buf.isEmpty()) {
            continue
          }
          buf.append(c)
          yieldSentence(buf)
          buf.clear()
          continue
        }
        buf.append(c)
      }
      if (buf.isNotBlank()) {
        yieldSentence(buf)
      }
    }
  }

  private suspend fun SequenceScope<Sentence>.yieldSentence(buf: StringBuilder) {
    val wholeText = buf.trim().toString()
    yield(Sentence(wholeText))
  }

  private fun isSentenceStopSymbol(c: Char): Boolean {
    return c in sentenceStopSymbols
  }

  private fun isWhitespace(c: Char): Boolean {
    return c.isWhitespace() || c == '\n'
  }
}