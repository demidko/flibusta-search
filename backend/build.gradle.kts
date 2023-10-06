import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
  mavenCentral()
  maven("https://jitpack.io")
  maven("https://repo.spring.io/milestone")
  maven("https://repo.spring.io/snapshot")
}
plugins {
  id("org.springframework.boot") version "3.2.0-SNAPSHOT"
  id("io.spring.dependency-management") version "1.1.3"
  kotlin("jvm") version "1.9.10"
  kotlin("plugin.spring") version "1.9.10"
}
dependencies {
  implementation("org.apache.lucene:lucene-analyzers-common:8.11.2")
  implementation("org.apache.lucene:lucene-queryparser:9.8.0")
  implementation("org.apache.commons:commons-csv:1.10.0")
  implementation("com.github.demidko:aot:2022.11.28")
  implementation("org.apache.commons:commons-collections4:4.4")
  implementation("org.apache.commons:commons-text:1.10.0")

  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.jetbrains.kotlin:kotlin-reflect")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("com.google.truth:truth:1.1.3")
  testImplementation("io.mockk:mockk:1.13.4")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
tasks.withType<KotlinCompile> {
  kotlinOptions.jvmTarget = "20"
  kotlinOptions.freeCompilerArgs += listOf(
    "-Xjsr305=strict",
    "-Xvalue-classes",
    "-opt-in=kotlin.ExperimentalStdlibApi",
    "-opt-in=kotlin.time.ExperimentalTime"
  )
}
tasks.withType<JavaCompile> {
  sourceCompatibility = "20"
  targetCompatibility = "20"
}
tasks.test {
  minHeapSize = "2048m"
  maxHeapSize = "4096m"
  useJUnitPlatform()
  jvmArgs("--enable-preview")
}
tasks.bootJar {
  archiveVersion.set("boot")
}
