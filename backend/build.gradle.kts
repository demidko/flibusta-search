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
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.apache.commons:commons-csv:1.10.0")
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
  useJUnitPlatform()
  jvmArgs("--enable-preview")
}
tasks.bootJar {
  archiveVersion.set("boot")
}
