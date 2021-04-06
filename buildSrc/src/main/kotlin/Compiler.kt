object CompilerArguments {
  const val experimentalCoroutines = "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
  const val requiresOptIn = "-Xopt-in=kotlin.RequiresOptIn"
}

object Jvm {
  const val target = "1.8"
}
