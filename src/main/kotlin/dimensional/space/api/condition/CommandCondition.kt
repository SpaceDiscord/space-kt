package dimensional.space.api.condition

import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.Command

interface CommandCondition {
  /**
   * @param ctx Command context
   * @param command Command being invoked
   *
   * @throws ConditionFailedException If this condition has failed
   */
  suspend fun handle(ctx: CommandContext, command: Command)
}