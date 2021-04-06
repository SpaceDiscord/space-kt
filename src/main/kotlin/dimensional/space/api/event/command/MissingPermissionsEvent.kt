package dimensional.space.api.event.command

import dev.kord.common.entity.Permission
import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.Command

class MissingPermissionsEvent(
  ctx: CommandContext, command: Command,

  /**
   * The permissions that were missing.
   */
  val permissions: List<Permission>,

  /**
   * The type of permissions check that failed
   */
  val type: PermissionsType
) : CommandFailedEvent(ctx, command) {
  enum class PermissionsType {
    Bot,
    User
  }
}