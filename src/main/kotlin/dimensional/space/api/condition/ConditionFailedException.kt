package dimensional.space.api.condition

import dimensional.space.api.command.CommandContext

class ConditionFailedException(val condition: CommandCondition, val ctx: CommandContext)
