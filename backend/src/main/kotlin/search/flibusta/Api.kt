package search.flibusta

import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import search.flibusta.entities.Bibliography
import search.flibusta.entities.QuotesCollection

@Validated
@RestController
class Api(private val catalog: FlibustaRussianCatalog, private val searcher: QuotesSearcher) {

  @GetMapping("/search")
  fun search(@NotBlank author: String, @NotBlank q: String): Collection<QuotesCollection> {
    return searcher.quotes(author, q)
  }

  @GetMapping("/authors")
  fun authors(@NotBlank query: String): List<Bibliography> {
    return catalog.searchAuthors(query)
  }

  @GetMapping("/download")
  fun download(id: Int) {
    TODO()
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(RuntimeException::class)
  fun handleRuntimeException(e: RuntimeException): String? {
    return e.message
  }
}