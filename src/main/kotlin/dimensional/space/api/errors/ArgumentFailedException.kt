package dimensional.space.api.errors

import dimensional.space.api.command.CommandParameter

class ArgumentFailedException(
  val parameter: CommandParameter<*>,
  val providedArgument: String,
  val original: Throwable? = null
) : Throwable("`${parameter.name}` must be a `${parameter.type.simpleName}`")
