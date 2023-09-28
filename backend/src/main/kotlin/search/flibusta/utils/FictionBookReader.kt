package search.flibusta.utils

import java.io.Closeable
import java.io.DataInputStream
import java.io.EOFException

class FictionBookReader(private val dataInputStream: DataInputStream) : Closeable {

  private val stopSymbols = ".?!\n".toSet()

  private val charIterator = generateSequence(dataInputStream::readChar).iterator()

  fun readSentence(): String? {
    return buildString {
      try {
        for (char in charIterator) {
          if (isTagStart(char)) {
            val tag = parseTag()
            if (isBinaryTag(tag)) {
              dropTagContent()
            }
            continue
          }
          if (isStopSymbol(char)) {
            if (isEmpty()) {
              continue
            }
            break
          }
          append(char)
        }
      } catch (e: EOFException) {
        return if (isEmpty()) null else toString()
      }
    }
  }

  fun sentenceSequence(): Sequence<String> {
    return generateSequence(::readSentence)
  }

  private fun parseTag(): String {
    val tagSequence = charIterator.asSequence().takeWhile { isTagEnd(it).not() }
    return tagSequence.toString().trimStart()
  }

  private fun isBinaryTag(tag: String): Boolean {
    return tag.startsWith("binary")
  }

  private fun dropTagContent() {
    charIterator.asSequence().dropWhile { isTagStart(it).not() }
    charIterator.asSequence().dropWhile { isTagEnd(it).not() }
  }

  private fun isTagStart(char: Char): Boolean {
    return char == '<'
  }

  private fun isTagEnd(char: Char): Boolean {
    return char == '>'
  }

  private fun isStopSymbol(char: Char): Boolean {
    return char in stopSymbols
  }

  override fun close() {
    dataInputStream.close()
  }
}