package dimensional.space.internal.conversion

import dev.kord.common.entity.Snowflake
import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.params.Converter

object SnowflakeConverter : Converter<Snowflake> {
  val snowflakePattern = "^(?:<(?:@!?|@&|#)(?<mid>[0-9]{16,21})>|(?<id>[0-9]{16,21}))\$".toPattern()

  override suspend fun convert(ctx: CommandContext, content: String): Snowflake? =
    snowflakePattern.matcher(content).takeIf { it.matches() }
      ?.let { matcher -> matcher.group("mid") ?: matcher.group("id") }
      ?.let { id -> Snowflake(id.toLong()) }
}
