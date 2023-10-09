package search.flibusta.utils

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test

class MorphAnalyzerTest {

  @Test
  fun queryBasisVariants() {
    val analyzer = MorphAnalyzer()
    val query = "Замок стоял под дождем"
    val basis = analyzer.queryBases(query)
    assertThat(basis).isEqualTo(setOf(
      setOf("замок", "стоять", "под", "дождь"),
      setOf("замок", "стоялый", "под", "дождь"),
      setOf("замокнуть", "стоять", "под", "дождь"),
      setOf("замокнуть", "стоялый", "под", "дождь")
    ))
  }
}