package search.flibusta

import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import search.flibusta.dto.Quotes
import search.flibusta.dto.Search
import search.flibusta.dto.Suggest
import java.io.File

@Validated
@RestController
class Api(private val quotesSearcher: QuotesSearcher, private val namesSearcher: NamesSearcher) {

  /**
   * Метод ищет цитаты из книг [author]'а.
   * 1. Если удалось найти совпадения с запросом, то возвращается объект, содержащий цитаты.
   * 2. Если найти цитаты не удалось, возвращается предложение, содержащее имена авторов,
   * которых мог иметь в виду пользователь.
   */
  @GetMapping("/search")
  fun search(@NotBlank author: String, @NotBlank query: String): Search {
    val quotes = quotesSearcher.searchQuotes(author, query)
    if (quotes.isEmpty()) {
      val names = namesSearcher.similarNames(author)
      return Suggest(names)
    }
    return Quotes(quotes)
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(RuntimeException::class)
  fun handleRuntimeException(e: RuntimeException): String? {
    return e.message
  }
}