package dimensional.space.api.command.args

import dimensional.space.api.command.CommandContext

/**
 * Argument converters allow user input to be converted into usable data or primitives
 *
 * @param T the class that will be returned
 */
interface Converter<T> {
  /**
   * Converts the provided [content] into [T]
   *
   * @param ctx Command context
   * @param content Content to resolve
   */
  suspend fun convert(ctx: CommandContext, content: String): T?
}
