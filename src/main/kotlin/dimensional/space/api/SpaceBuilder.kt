package dimensional.space.api

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dimensional.space.api.command.PrefixStrategy
import dimensional.space.api.command.params.ConversionStrategy
import dimensional.space.api.command.ratelimit.RateLimitStrategy
import dimensional.space.api.event.SpaceEvent
import dimensional.space.internal.command.DefaultRateLimitStrategy
import dimensional.space.internal.conversion.ConverterManager
import dimensional.space.internal.conversion.DefaultConversionStrategy
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.Executors

class SpaceBuilder(val kord: Kord) {
  /**
   * All converters to use.
   */
  val converters = ConverterManager()

  /**
   * Conversion strategies convert user inputted arguments into command parameters.
   * Only use this if you want custom argument parsing.
   */
  var conversionStrategy: ConversionStrategy = DefaultConversionStrategy(converters)

  /**
   * Prefix strategy to use when processing commands
   */
  var prefixStrategy: PrefixStrategy = PrefixStrategy {
    listOf("!")
  }

  /**
   * The rate-limit strategy
   */
  var rateLimitStrategy: RateLimitStrategy? = null

  /**
   * List of User IDs to view as "developers"
   */
  var ownerIds = hashSetOf<Long>()

  /**
   * Dispatcher to use
   */
  var dispatcher = Dispatchers.Default

  /**
   * The event flow used by [Space.events] to publish [SpaceEvent].
   */
  var eventFlow: MutableSharedFlow<SpaceEvent> = MutableSharedFlow(
    extraBufferCapacity = Int.MAX_VALUE
  )

  /**
   * Whether to ignore bots when processing messages
   */
  var ignoreBots: Boolean = true

  /**
   * Whether to ignore ourself, takes priority over [ignoreBots]
   */
  var ignoreSelf: Boolean = true

  /**
   * The number of threads used to execute commands. Defaults to twice the number of available processors.
   */
  var threads = Runtime.getRuntime().availableProcessors() * 2

  /**
   * The coroutine dispatcher used to execute commands
   */
  var commandDispatcher: CoroutineDispatcher? = null

  fun build(): Space {
    if (commandDispatcher == null) {
      commandDispatcher = Executors
        .newFixedThreadPool(threads)
        .asCoroutineDispatcher()
    }

    if (rateLimitStrategy == null) {
      rateLimitStrategy = DefaultRateLimitStrategy()
    }

    return Space(
      kord = kord,
      conversionStrategy = conversionStrategy,
      prefixStrategy = prefixStrategy,
      rateLimitStrategy = rateLimitStrategy!!,
      ignoreBots = ignoreBots,
      ignoreSelf = ignoreSelf,
      eventFlow = eventFlow,
      dispatcher = dispatcher,
      commandDispatcher = commandDispatcher!!,
      ownerIds = ownerIds.map { Snowflake(it) }.toHashSet()
    )
  }
}
