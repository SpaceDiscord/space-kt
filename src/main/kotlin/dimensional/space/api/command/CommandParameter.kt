package dimensional.space.api.command

import kotlin.reflect.KClass

data class CommandParameter<T : Any>(
  val type: KClass<T>,
  val name: String,
  val default: T?,
  val isGreedy: Boolean = false,
  val isNullable: Boolean = false,
  val isTentative: Boolean = false,
  val isLazy: Boolean = true
) {
  /**
   * Whether this parameter is optional.
   */
  val isOptional: Boolean
    get() = default != null

  /**
   * Closing & opening brackets
   */
  val brackets = when {
    isOptional || isNullable -> Pair("[", "]")
    isTentative -> Pair("(", ")")
    else -> Pair("<", ">")
  }

  /**
   * Resolved value, because lazy
   */
  private var resolved: T? = null

  /**
   * Formats this command argument.
   */
  fun format(withType: Boolean = true): String {
    val (opening, closing) = brackets
    return "$opening$name${if (withType) ": ${type.simpleName}" else ""}$closing"
  }
}
