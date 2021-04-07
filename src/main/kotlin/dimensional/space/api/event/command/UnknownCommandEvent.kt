package dimensional.space.api.event.command

import dimensional.space.api.Space
import dimensional.space.api.command.CommandContext
import dimensional.space.api.event.SpaceEvent

class UnknownCommandEvent(
  /**
   * The command context
   */
  val ctx: CommandContext,
) : SpaceEvent {
  override val space: Space
    get() = ctx.space

  /**
   * The prefix used
   */
  val prefix: String
    get() = ctx.prefix

  /**
   * The trigger used
   */
  val trigger: String
    get() = ctx.trigger
}
