package search.flibusta

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.net.URI
import java.util.concurrent.TimeUnit.MINUTES

@EnableScheduling
@SpringBootApplication
class App {

  @Bean
  fun catalog(): Catalog {
    val uri = URI("https://flibusta.is/catalog/catalog.zip")
    val url = uri.toURL()
    return Catalog(url)
  }

  @Scheduled(fixedDelay = 15, timeUnit = MINUTES)
  fun updateCatalog() {
    catalog().updateCatalog()
  }
}

fun main(args: Array<String>) {
  runApplication<App>(*args)
}
