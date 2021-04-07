import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.event.gateway.ReadyEvent
import dev.kord.core.on
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.PrivilegedIntent
import dimensional.space.api.Space
import dimensional.space.api.command.PrefixStrategy
import dimensional.space.api.command.command
import dimensional.space.api.command.params.parameter
import dimensional.space.api.event.command.UnknownCommandEvent
import dimensional.space.api.on
import dimensional.space.internal.util.addDefaultConverters
import kotlin.math.round
import kotlin.system.measureTimeMillis
import kotlin.time.ExperimentalTime

@PrivilegedIntent
@OptIn(ExperimentalTime::class)
suspend fun main(args: Array<String>) {
  val kord = Kord(args.firstOrNull() ?: error("provide token pls")) {
    defaultStrategy = EntitySupplyStrategy.cacheWithRestFallback
    intents = Intents {
      +Intent.GuildMembers
      +Intent.GuildMessages
    }
  }

  val space = Space(kord) {
    prefixStrategy = PrefixStrategy { listOf("cum ") }
    converters.addDefaultConverters()
  }

  kord.on<ReadyEvent> {
    println("ready!")
  }

  space.on<UnknownCommandEvent> {
    ctx.reply("bruh, command `${trigger}` doesn't even exist")
  }

  space.commands += command {
    name = "ping"

    parameters.add(parameter<Int>("hi"))

    onInvoke { ctx ->
      val param = ctx.params.getOrNull<Int>("hi")
      println(param?.times(4) ?: "ok")

      var msg: Message
      val time = measureTimeMillis {
        msg = ctx.reply {
          description = """**Pong!**
            | Heartbeat: ${kord.gateway.averagePing?.let { "*${round(it.inMilliseconds)}ms*" } ?: "N/A"}
          """.trimMargin()
        }
      }

      msg.edit {
        embed {
          description = """
            ${msg.embeds.first().description}
            | Rest: *${time}ms*
          """.trimIndent()
        }
      }
    }
  }

  kord.login()
}