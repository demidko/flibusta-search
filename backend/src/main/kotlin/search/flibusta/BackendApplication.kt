package search.flibusta

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import java.util.concurrent.TimeUnit.MINUTES

@EnableScheduling
@SpringBootApplication
class BackendApplication {

  @Value("\${flibusta.mirror:https://flibusta.is}")
  private lateinit var mirror: String

  @Bean
  fun catalog(): FlibustaRussianCatalog {
    return FlibustaRussianCatalog(mirror)
  }

  @Bean
  fun downloader(): FlibustaDownloader {
    return FlibustaDownloader(mirror)
  }

  @Bean
  fun searcher(): QuotesSearcher {
    return QuotesSearcher(catalog(), downloader())
  }

  @Scheduled(fixedDelay = 30, timeUnit = MINUTES, initialDelay = 30)
  fun updateCatalog() {
    catalog().updateCatalog()
  }

  @Scheduled(fixedDelay = 6, timeUnit = MINUTES, initialDelay = 6)
  fun clearCache() {
    downloader().clearCache()
  }
}

fun main(args: Array<String>) {
  runApplication<BackendApplication>(*args)
}
