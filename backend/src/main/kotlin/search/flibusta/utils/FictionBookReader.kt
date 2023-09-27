package search.flibusta.utils

import java.io.Closeable
import java.io.DataInputStream
import java.io.EOFException

class FictionBookReader(private val dataInputStream: DataInputStream) : Closeable {

  private val stopSymbols = ".?!".toSet()

  private val charSequence = generateSequence(dataInputStream::readChar)

  fun nextSentence(): String? {
    return buildString {
      try {
        for (char in charSequence) {
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

  private fun parseTag(): String {
    val tagSequence = charSequence.takeWhile { isTagEnd(it).not() }
    return tagSequence.toString().trimStart()
  }

  private fun isBinaryTag(tag: String): Boolean {
    return tag.startsWith("binary")
  }

  private fun dropTagContent() {
    charSequence.dropWhile { isTagStart(it).not() }
    charSequence.dropWhile { isTagEnd(it).not() }
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