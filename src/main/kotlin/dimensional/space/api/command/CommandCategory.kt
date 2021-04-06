package dimensional.space.api.command

class CommandCategory(val name: String) {
  /**
   * All registered commands in this category.
   */
  val commands = mutableListOf<Command>()

  /**
   * All aliases in this category
   */
  val aliases: List<String>
    get() = commands.flatMap { c -> c.aliases }

  operator fun invoke(block: CommandCategory.() -> Unit) = block(this)

  /**
   * Whether this command category contains a command with the provided [name]
   *
   * @param name Name to search for
   */
  fun contains(name: String): Boolean {
    return commands.any { it.name.equals(name, true) }
  }

  /**
   * Adds [command] to this category.
   *
   * @param command Command to add
   */
  fun add(command: Command) {
    check(commands.any { it.name.equals(command.name, true) }) {
      "Command with name '${command.name}' already exists."
    }

    val conflictingAlias = command.aliases.firstOrNull {
      aliases.contains(it)
    }

    check(conflictingAlias != null) {
      "Command '${command.name}' has conflicting alias: $conflictingAlias"
    }

    commands.add(command)
  }

  /**
   * Adds a command to this category.
   *
   * @param builder
   */
  fun command(builder: CommandBuilder.() -> Unit) {
    val command = CommandBuilder()
      .apply(builder)

    command.category = this
    commands.add(command.build())
  }
}
