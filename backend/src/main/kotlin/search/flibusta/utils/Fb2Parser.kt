package search.flibusta.utils

import com.github.demidko.aot.WordformMeaning
import com.github.demidko.aot.WordformMeaning.lookupForMeanings
import org.w3c.dom.Node
import search.flibusta.utils.StringUtils.simpleStemOf
import search.flibusta.utils.StringUtils.whileSplit
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.util.zip.ZipInputStream
import javax.xml.parsers.DocumentBuilderFactory

object Fb2Parser {

  private val factory = DocumentBuilderFactory.newInstance()

  private val sentenceStopSymbols = setOf('?', '!', '.')

  fun sentencesOf(fb2: File): Sequence<Sentence> {
    val zip = ZipInputStream(BufferedInputStream(FileInputStream(fb2)))
    zip.use {
      val entry = zip.nextEntry
      requireNotNull(entry) { "Invalid book $fb2" }
      val filename = entry.name
      require(filename.endsWith(".fb2")) { "$fb2: expected fb2, but found $filename" }
      val document = factory.newDocumentBuilder().parse(zip)
      val nodes = document.getElementsByTagName("body")
      require(nodes.length == 1)
      val body = nodes.item(0)
      require(body.hasChildNodes())
      return sentencesOf(body)
    }
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
    val lemmas = mutableSetOf<WordformMeaning>()
    val stems = mutableSetOf<String>()
    whileSplit(wholeText) {
      stems.add(simpleStemOf(it))
      for (meaning in lookupForMeanings(it)) {
        lemmas.add(meaning.lemma)
      }
    }
    yield(Sentence(wholeText, lemmas, stems))
  }

  private fun isSentenceStopSymbol(c: Char): Boolean {
    return c in sentenceStopSymbols
  }

  private fun isWhitespace(c: Char): Boolean {
    return c.isWhitespace() || c == '\n'
  }
}