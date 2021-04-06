package dimensional.space.api.event.command

import dimensional.space.api.command.Command
import dimensional.space.api.command.CommandContext

class CommandRateLimitedEvent(
  ctx: CommandContext,
  command: Command,

  /**
   * Number of milliseconds until the rate-limit expires.
   */
  val expiresIn: Long,

  /**
   * Id of the entity that got rate-limited
   */
  val entityId: Long,
) : CommandFailedEvent(ctx, command)
