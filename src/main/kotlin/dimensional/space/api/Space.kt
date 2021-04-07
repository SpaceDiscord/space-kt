package dimensional.space.api

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.kordLogger
import dev.kord.core.on
import dimensional.space.api.command.Command
import dimensional.space.api.command.params.Parameters
import dimensional.space.api.command.PrefixStrategy
import dimensional.space.api.command.params.ConversionData
import dimensional.space.api.command.params.ConversionStrategy
import dimensional.space.api.command.ratelimit.RateLimitStrategy
import dimensional.space.api.event.SpaceEvent
import dimensional.space.api.event.command.CommandRateLimitedEvent
import dimensional.space.api.event.command.DeveloperOnlyEvent
import dimensional.space.api.event.command.GuildOnlyEvent
import dimensional.space.api.event.command.UnknownCommandEvent
import dimensional.space.internal.CommandContextImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.coroutines.CoroutineContext

class Space(
  val kord: Kord,
  val conversionStrategy: ConversionStrategy,
  val prefixStrategy: PrefixStrategy,
  val rateLimitStrategy: RateLimitStrategy,
  val ignoreBots: Boolean,
  val ignoreSelf: Boolean,
  val ownerIds: HashSet<Snowflake>,
  private val eventFlow: MutableSharedFlow<SpaceEvent>,
  private val dispatcher: CoroutineDispatcher,
  val commandDispatcher: CoroutineDispatcher
) : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = dispatcher + Job()

  /**
   * List of all registered commands.
   */
  val commands: MutableList<Command> = mutableListOf()

  /**
   * Events emitted by [Space].
   */
  val events: SharedFlow<SpaceEvent>
    get() = eventFlow

  init {
    kord.on<MessageCreateEvent> {
      if (message.webhookId != null) {
        return@on
      }

      val self = ignoreSelf && message.author!!.id == kord.selfId
      if (ignoreBots && (!self && message.author!!.isBot)) {
        return@on
      }

      val ctx = CommandContextImpl(this@Space, message)

      /* get prefix being used */
      val prefixes = prefixStrategy.get(ctx)
      val prefix = prefixes.firstOrNull { message.content.startsWith(it, true) }
        ?: return@on

      if (prefix.length == message.content.length) {
        return@on
      }

      /* get args and command trigger */
      val args = message.content.drop(prefix.length).split(" +".toRegex()).toMutableList()
      val trigger = args.removeFirst().toLowerCase()

      /* assign values to context */
      ctx.prefix = prefix
      ctx.trigger = trigger

      /* get command or dispatch unknown command event */
      val command = commands.firstOrNull { it.triggeredBy(trigger) }
      if (command == null) {
        eventFlow.emit(UnknownCommandEvent(ctx))
        return@on
      }

      ctx.command = command

      /* check rate-limit */
      val entityId = command.rateLimit?.let { RateLimitStrategy.getEntityId(ctx, it.bucket) }
      if (entityId != null && rateLimitStrategy.isLimited(entityId, command, command.rateLimit)) {
        val expiresIn = rateLimitStrategy.getRemaining(entityId, command, command.rateLimit)
          ?: return@on

        return@on eventFlow.emit(CommandRateLimitedEvent(ctx, command, expiresIn, entityId))
      }

      /* check developer only */
      if (command.restrictions.developerOnly && ownerIds.contains(ctx.author.id)) {
        eventFlow.emit(DeveloperOnlyEvent(ctx, command))
        return@on
      }

      /* guild related checks */
      val guild = ctx.getGuild()
      if (guild != null) {
        if (command.restrictions.clientPerms.isNotEmpty()) {
          val me = guild.getMember(kord.selfId)
          me.getPermissions()
        }
      } else if (command.restrictions.guildOnly) {
        eventFlow.emit(GuildOnlyEvent(ctx, command))
        return@on
      }

      /* get parameters */
      val params = if (command.parameters.isNotEmpty()) {
        conversionStrategy.consume(ctx, ConversionData(
          input = args,
          delimiter = ' ',
          parameters = command.parameters
        ))
      } else {
        hashMapOf()
      }

      ctx.params = Parameters(params)

      /* execute command */
      commandDispatcher.invoke {
        command.onInvoke(ctx)
      }
    }
  }

  /**
   * Registers the provided [command]
   *
   * @param command Command to register
   */
  fun registerCommand(command: Command) {
    commands.add(command)
  }
}

/**
 * Builds an instance of [Space] using the supplied [builder]
 *
 * @param kord Kord instance to use
 * @param builder Space builder
 */
@OptIn(ExperimentalContracts::class)
fun Space(kord: Kord, builder: SpaceBuilder.() -> Unit): Space {
  contract {
    callsInPlace(builder, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
  }

  return SpaceBuilder(kord)
    .apply(builder)
    .build()
}


/**
 * Convenience method that will invoke the [consumer] on every event [T] created by [Space.events].
 *
 * @param scope Coroutine scope
 * @param consumer
 *
 * @return Job, used to cancel any further processing of [T]
 */
inline fun <reified T : SpaceEvent> Space.on(
  scope: CoroutineScope = this,
  noinline consumer: suspend T.() -> Unit
): Job =
  events.buffer(Channel.UNLIMITED).filterIsInstance<T>()
    .onEach {
      scope.launch {
        runCatching { consumer(it) }
          .onFailure {
            kordLogger.catching(it)
          }
      }
    }
    .launchIn(scope)

