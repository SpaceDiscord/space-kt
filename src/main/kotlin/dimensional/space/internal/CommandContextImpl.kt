package dimensional.space.internal

import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dimensional.space.api.command.CommandContext
import dimensional.space.api.Space
import dimensional.space.api.command.Command
import dimensional.space.api.command.CommandParameters

class CommandContextImpl(
  override val space: Space,
  override val message: Message
) : CommandContext {
  override lateinit var args: CommandParameters
  override lateinit var command: Command
  override lateinit var prefix: String
  override lateinit var trigger: String

  override suspend fun getGuild(): Guild? =
    message.getGuildOrNull()

  override suspend fun getChannel(): MessageChannel =
    message.getChannel()

  override suspend fun reply(content: String, builder: MessageCreateBuilder.() -> Unit): Message = getChannel()
    .createMessage {
      this.messageReference = message.id
      this.content = content
      builder()
    }

  override suspend fun reply(builder: EmbedBuilder.() -> Unit): Message = getChannel()
    .createMessage {
      this.messageReference = message.id
      embed(builder)
    }
}