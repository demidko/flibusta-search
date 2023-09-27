package search.flibusta

import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import search.flibusta.dto.Search
import search.flibusta.dto.Suggestion

@Validated
@RestController
class ApiController(private val catalog: Catalog) {

  @GetMapping("/search")
  fun search(@NotBlank author: String, @NotBlank words: String): Search {
    val wordsList = words.split(" ")
    val booksToQuotes = catalog.searchBooksQuotes(author, wordsList)
    if (booksToQuotes.isEmpty()) {
      val possibleAuthors = catalog.similarAuthors(author)
      return Suggestion(possibleAuthors)
    }
    return Result(booksToQuotes)
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(RuntimeException::class)
  fun handleConstraintViolationException(e: RuntimeException): String? {
    return e.message
  }
}