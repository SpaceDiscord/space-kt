@file:OptIn(ExperimentalContracts::class)

package dimensional.space.api.command

import dev.kord.common.entity.Permission
import dimensional.space.api.command.ratelimit.RateLimit
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias CommandInvocation = suspend (CommandContext) -> Unit
typealias InlinedCommandCondition = suspend (CommandContext) -> Unit

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
   * Permissions required by the invoker
   */
  var userPerms = Items<Permission>()

  /**
   * Permissions required by us
   */
  var clientPerms = Items<Permission>()

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
  var parameters = hashMapOf<String, CommandParameter<*>>()

  /**
   * Aliases of this command
   */
  var aliases = Items<String>()

  /**
   * Handles the invocation of this command
   */
  var invokeHandler: CommandInvocation? = null

  /**
   *
   */
  fun onInvoke(block: CommandInvocation) {
    contract {
      callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    invokeHandler = block
  }

  fun build(): Command {
    check(name.isNullOrEmpty()) {
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
      onInvoke = invokeHandler!!
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
