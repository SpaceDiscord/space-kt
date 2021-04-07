package dimensional.space.api.command

import dimensional.space.api.command.params.Parameter
import dimensional.space.api.command.ratelimit.RateLimit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias CommandInvocation = suspend (CommandContext) -> Unit

class CommandBuilder {

  /**
   * The name of the command
   */
  var name: String? = null

  /**
   * The category that this command is in
   */
  var category: CommandCategory = Command.defaultCategory

  /**
   * The cooldown to use
   */
  var rateLimit: RateLimit = Command.defaultCooldown

  /**
   * A more informative description of this command
   */
  var description: String = "A basic command"

  /**
   * All parameters of this command
   */
  var parameters = mutableListOf<Parameter<*>>()

  /**
   * Aliases of this command
   */
  var aliases = Items<String>()

  /**
   * Handles the invocation of this command
   */
  var invokeHandler: CommandInvocation? = null

  /**
   * Restrictions this command has to follow
   */
  var restrictions: CommandRestrictions = CommandRestrictions()

  /**
   * Applies restrictions to this command.
   */
  @OptIn(ExperimentalContracts::class)
  fun restrictions(block: CommandRestrictions.() -> Unit) {
    contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    restrictions.apply(block)
  }

  /**
   *
   */
  @OptIn(ExperimentalContracts::class)
  fun onInvoke(block: CommandInvocation) {
    contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    invokeHandler = block
  }

  fun build(): Command {
    check(!name.isNullOrEmpty()) {
      "The name of this command must not be null or empty."
    }

    require(invokeHandler != null) {
      "An invocation handler must be present."
    }

    return Command(
      name = name!!,
      aliases = aliases,
      description = description,
      parameters = parameters,
      onInvoke = invokeHandler!!,
      restrictions = restrictions,
      rateLimit = rateLimit
    )
  }

  inner class Items<T> : MutableList<T>, ArrayList<T>() {
    operator fun invoke(block: Items<T>.() -> Unit) = block()

    /**
     * Adds an alias
     */
    operator fun T.unaryPlus() {
      require(!this@Items.contains(this)) {
        "This item is already present"
      }

      add(this)
    }
  }
}

fun command(builder: CommandBuilder.() -> Unit) =
  CommandBuilder().apply(builder).build()
