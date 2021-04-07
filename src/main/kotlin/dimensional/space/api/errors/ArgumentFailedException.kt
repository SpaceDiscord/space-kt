package dimensional.space.api.errors

import dimensional.space.api.command.params.Parameter

class ArgumentFailedException(
  val parameter: Parameter<*>,
  val providedArgument: String,
  val original: Throwable? = null
) : Throwable("`${parameter.name}` must be a `${parameter.type.simpleName}`")
