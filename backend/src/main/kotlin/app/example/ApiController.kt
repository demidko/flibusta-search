package app.example

import jakarta.validation.ConstraintViolationException
import jakarta.validation.constraints.NotBlank
import org.springframework.http.HttpStatus.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Validated
@RestController
class ApiController {

  @GetMapping("/register")
  fun register(@NotBlank name: String): String {
    TODO()
  }

  @PostMapping("/feed")
  fun feed(@NotBlank token: String, date: LocalDate): List<String> {
    TODO()
  }

  @PostMapping("/change-token")
  fun changeToken(@NotBlank token: String): String {
    TODO()
  }

  @GetMapping("/post")
  fun post(@NotBlank token: String, @NotBlank title: String, @NotBlank text: String) {
    TODO()
  }

  @ResponseStatus(BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException::class)
  fun handleConstraintViolationException(e: ConstraintViolationException): String? {
    return e.message
  }
}