package dimensional.space.api.command.params

import dimensional.space.api.command.CommandContext

/**
 * Consumes input from a User and resolves it into usable primitives or classes.
 */
interface ConversionStrategy {
  /**
   * The name of this strategy.
   */
  val name: String

  /**
   * Converts [ConversionData.input] into usable primitives or data
   *
   * @param ctx Command context
   * @param data User-input, delimiter to use, and the arguments
   */
  suspend fun consume(ctx: CommandContext, data: ConversionData): HashMap<String, Any?>
}

data class ConversionData(
  val input: List<String>,
  val delimiter: Char,
  val parameters: List<Parameter<*>>
)
