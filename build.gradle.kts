import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  kotlin("jvm") version Versions.kotlin
}

group = "fun.dimensional"
version = "1.0.0"

repositories {
  mavenCentral()
  jcenter()

  maven("https://oss.sonatype.org/content/repositories/snapshots")
  maven("https://m2.dv8tion.net/releases")
}

dependencies {
  /* kotlinx coroutines */
  implementation(Dependencies.kotlin)
  implementation(Dependencies.kotlinReflect)
  implementation(Dependencies.`kotlinx-coroutines`)

  /* logging */
  implementation(Dependencies.kotlinLogging)

  /* discord */
  api("dev.kord:kord-core:0.7.0-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    useIR = true
    jvmTarget = Jvm.target
    freeCompilerArgs = listOf(
      CompilerArguments.experimentalCoroutines,
      CompilerArguments.requiresOptIn
    )
  }
}
