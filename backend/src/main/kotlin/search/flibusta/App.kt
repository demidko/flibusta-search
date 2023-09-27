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
    val catalogUri = URI("https://flibusta.is/catalog/catalog.zip")
    val catalogUrl = catalogUri.toURL()
    return Catalog(catalogUrl)
  }

  @Bean
  fun downloader(): Downloader {
    return Downloader()
  }

  @Bean
  fun quotesSearcher(): QuotesSearcher {
    return QuotesSearcher(catalog(), downloader())
  }

  @Bean
  fun namesSearcher(): NamesSearcher {
    return NamesSearcher(catalog())
  }

  @Scheduled(fixedDelay = 15, timeUnit = MINUTES)
  fun updateCatalog() {
    catalog().updateCatalog()
  }
}

fun main(args: Array<String>) {
  runApplication<App>(*args)
}
