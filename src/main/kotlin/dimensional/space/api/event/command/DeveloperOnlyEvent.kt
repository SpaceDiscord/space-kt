package dimensional.space.api.event.command

import dev.kord.core.entity.User
import dimensional.space.api.command.Command
import dimensional.space.api.command.CommandContext

class DeveloperOnlyEvent(ctx: CommandContext, command: Command) : CommandFailedEvent(ctx, command) {
  /**
   * User that invoked [command]
   */
  val user: User
    get() = ctx.author
}