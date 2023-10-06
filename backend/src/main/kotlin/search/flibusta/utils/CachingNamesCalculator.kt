package search.flibusta.utils

class CachingNamesCalculator(name: String) {

  private val multiName = MultiName(name)

  private val cachedDistances = mutableMapOf<String, Int>()

  fun calculateDistance(otherName: String): Int {
    return cachedDistances.getOrPut(otherName) {
      multiName.calculateDistance(MultiName(otherName))
    }
  }
}