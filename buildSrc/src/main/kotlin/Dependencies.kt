object Versions {
  const val kotlin = "1.4.32"
  const val kotlinLogging = "2.0.4"
  const val kotlinxCoroutines = "1.4.2"

  const val slf4j = "1.7.30"
  const val jda = "4.2.1_253"
}

object Dependencies {
  const val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
  const val kotlinReflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"
  const val `kotlinx-coroutines` = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinxCoroutines}"

  const val jda = "net.dv8tion:JDA:${Versions.jda}"

  const val kotlinLogging = "io.github.microutils:kotlin-logging:${Versions.kotlinLogging}"
}
