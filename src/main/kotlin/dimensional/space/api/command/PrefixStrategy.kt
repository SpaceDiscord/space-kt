package dimensional.space.api.command

fun interface PrefixStrategy {
  /**
   * Returns a list of prefixes that can invoke commands
   */
  fun get(ctx: CommandContext): List<String>
}
