package dimensional.space.internal.util

import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.*
import dimensional.space.internal.conversion.ConverterManager
import dimensional.space.internal.conversion.SnowflakeConverter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull

/**
 * Adds all of the default resolvers to the [ConverterManager.registry]
 */
fun ConverterManager.addDefaultConverters() {
  addPrimitiveConverters()
  addKordConverters()
}

/**
 * Adds resolvers for primitive types
 */
fun ConverterManager.addPrimitiveConverters() {
  addConverter { _, c ->
    when (c) {
      "yes", "y", "true", "t", "yah", "yessir", "mhm", "fax", "no cap", "no means no" -> true
      "no", "n", "false", "f", "nah", "cap" -> false
      else -> null
    }
  }

  addConverter { _, c -> c.toInt() }

  addConverter { _, c -> c.toLong() }

  addConverter { _, c -> c.toDouble() }

  addConverter { _, c -> c.toByte() }

  addConverter { _, c -> c.toFloat() }
}

/**
 * Adds resolvers for Kord entities..
 */
fun ConverterManager.addKordConverters() {
  registry[Snowflake::class.java] = SnowflakeConverter

  /* member */
  addConverter { ctx, content ->
    ctx.getGuild()?.members?.firstOrNull { member ->
      SnowflakeConverter.convert(ctx, content)?.let { it == member.id }
        ?: member.tag.equals(content, true)
        || member.username.equals(content, true)
    }
  }

  /* user */
  addConverter { ctx, content ->
    ctx.kord.users.firstOrNull { user ->
      SnowflakeConverter.convert(ctx, content)?.let { it == user.id }
        ?: user.tag.equals(content, true)
        || user.username.equals(content, true)
    }
  }

  /* role */
  addConverter { ctx, content ->
    ctx.getGuild()?.roles?.firstOrNull { role ->
      SnowflakeConverter.convert(ctx, content)?.let { it == role.id }
        ?: role.name.equals(content, true)
    }
  }

  /* ban */
  addConverter { ctx, content ->
    ctx.getGuild()?.bans?.firstOrNull { ban ->
      SnowflakeConverter.convert(ctx, content)?.let { it == ban.userId }
        ?: ban.user.asUserOrNull()?.let { user ->
          user.username.equals(content, true)
            || user.tag.equals(content, true)
        }
        ?: false
    }
  }

  val messageUrlRegex =
    "https?://(?:(?:canary|ptb)\\.)?discord(?:app)?\\.com/channels/(?:@me|(?<g>\\d+))/(?<c>\\d+)/(?<m>\\d+)".toPattern()

  /* message */
  addConverter { ctx, content ->
    val matcher = messageUrlRegex.matcher(content).takeIf {
      it.matches()
    }

    val channel = matcher?.let {
      val channelId = it.group("c")?.toLongOrNull()
        ?: return@let null

      ctx.getGuild()?.channels?.filterIsInstance<MessageChannel>()
        ?.firstOrNull { channel ->
          channel.id == Snowflake(channelId)
        }
    }
      ?: ctx.getChannel()

    val messageId = (matcher?.group("m") ?: content).toLongOrNull()
      ?: return@addConverter null

    channel.getMessage(Snowflake(messageId))
  }

  /* channels */
  channelResolver<TextChannel>()

  channelResolver<VoiceChannel>()

  channelResolver<GuildChannel>()

  channelResolver<Category>()

  channelResolver<StoreChannel>()

  channelResolver<NewsChannel>()
}

/**
 * Adds a resolver for Channel type [T]
 */
private inline fun <reified T : GuildChannel> ConverterManager.channelResolver() {
  addConverter { ctx, content ->
    ctx.getGuild()?.channels
      ?.filterIsInstance<T>()
      ?.firstOrNull {
        SnowflakeConverter.convert(ctx, content)
          ?.let { id -> it.id == id }
          ?: it.name.equals(content, true)
      }
  }
}
