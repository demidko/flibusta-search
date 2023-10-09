package search.flibusta

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import search.flibusta.dto.Bibliography
import search.flibusta.utils.ResponseStreamer

@Validated
@RestController
class ApiController(private val catalog: FlibustaRussianCatalog, private val searcher: QuotesSearcher) {

  private val mapper = jacksonObjectMapper()

  @GetMapping("/search")
  fun search(@NotBlank author: String, @NotBlank q: String): StreamingResponseBody {
    return StreamingResponseBody {
      val writer = it.bufferedWriter()
      val responseStreamer = ResponseStreamer(writer, mapper)
      searcher.searchQuotes(author, q, responseStreamer)
    }
  }

  @GetMapping("/debug")
  fun search(@NotBlank author: String): List<Bibliography> {
    return catalog.searchAuthors(author)
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(RuntimeException::class)
  fun handleRuntimeException(e: RuntimeException): String? {
    return e.message
  }
}