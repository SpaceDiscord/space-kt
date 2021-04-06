package dimensional.space.api.command

import dimensional.space.api.command.ratelimit.RateLimit

data class Command(
  val name: String,
  val parameters: HashMap<String, CommandParameter<*>>,
  val description: String,
  val aliases: List<String>,
  val onInvoke: CommandInvocation,

) {
  /**
   * Phrases that will trigger this command.
   */
  val triggers: List<String>
    get() = listOf(*aliases.toTypedArray(), name)

  init {
    if (defaultCategory.contains(name)) {
      defaultCategory.add(this)
    }
  }

  /**
   * Whether the provided [phrase] triggers this command.
   *
   * @param phrase Phrase to check.
   */
  fun triggeredBy(phrase: String): Boolean = triggers.any { trigger ->
    trigger.equals(phrase, true)
  }

  companion object {
    /**
     * The default category for all commands
     */
    var defaultCategory = CommandCategory("general")

    /**
     * The default cooldown for all commands, defaults to 2 per-user invocations every 5 seconds
     */
    var defaultCooldown = RateLimit(5000, 2)
  }
}