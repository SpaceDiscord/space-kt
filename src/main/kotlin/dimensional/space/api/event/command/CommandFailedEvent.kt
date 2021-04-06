package dimensional.space.api.event.command

import dimensional.space.api.command.CommandContext
import dimensional.space.api.Space
import dimensional.space.api.command.Command
import dimensional.space.api.event.SpaceEvent

/**
 * Indicates that a [command] has failed to execute, due to
 */
abstract class CommandFailedEvent(
  /**
   * The current [CommandContext] being used
   */
  val ctx: CommandContext,

  /**
   * Command that failed
   */
  val command: Command
) : SpaceEvent {
  override val space: Space
    get() = ctx.space
}
