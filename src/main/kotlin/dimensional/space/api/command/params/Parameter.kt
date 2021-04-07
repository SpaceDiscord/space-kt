package dimensional.space.api.command.params

data class Parameter<T>(
  val type: Class<T>,
  val name: String,
  val default: T?,
  val isGreedy: Boolean = false,
  val isNullable: Boolean = false,
  val isTentative: Boolean = false,
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
   * Formats this command argument.
   */
  fun format(withType: Boolean = true): String {
    val (opening, closing) = brackets
    return "$opening$name${if (withType) ": ${type.simpleName}" else ""}$closing"
  }

  class Builder<T> {
    /**
     * The name of this parameter
     */
    var name: String? = null

    /**
     * Type of parameter this is
     */
    var type: Class<T>? = null

    /**
     * The default value of this parameter
     */
    var default: T? = null

    /**
     * Whether this parameter is tentative.
     */
    var tentative: Boolean = false

    /**
     * Whether this parameter is greedy
     */
    var greedy: Boolean = false

    fun build(): Parameter<T> {
      require(!name.isNullOrBlank()) {
        "Parameter must have a name"
      }

      require(type != null) {
        "Parameter must have a type"
      }

      return Parameter(
        name = name!!,
        type = type!!,
        default = default,
        isGreedy = greedy,
        isTentative = tentative,
        isNullable = default == null
      )
    }
  }
}

inline fun <reified T> parameter(name: String, builder: Parameter.Builder<T>.() -> Unit = {}) =
  Parameter.Builder<T>().apply {
    this.name = name
    this.type = T::class.java

    builder()
  }.build()
