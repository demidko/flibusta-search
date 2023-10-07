package search.flibusta

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import search.flibusta.utils.MultiName

object FlibustaRussianCatalogTest {

  val catalog by lazy { FlibustaRussianCatalog("https://flibusta.is") }

  @Test
  fun testSearchAuthor() {
    /**
     * Todo. В идеале хотелось бы добиться результата, когда запрос "Кира Райли" находил бы "Кору Рейли"
     */
  }
}