package dimensional.space.api.command

import dev.kord.core.Kord
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.rest.builder.message.EmbedBuilder
import dev.kord.rest.builder.message.MessageCreateBuilder
import dimensional.space.api.Space

interface CommandContext {
  /**
   * Space instance
   */
  val space: Space

  /**
   * The message that invoked the [command].
   */
  val message: Message

  /**
   * The command that was invoked
   */
  val command: Command

  /**
   * The prefix that was used
   */
  val prefix: String

  /**
   * The phrase that invoked the [command]
   */
  val trigger: String

  /**
   * Command arguments
   */
  val args: CommandParameters

  /**
   * The User that invoked the [command]
   */
  val author: User
    get() = message.author!!

  /**
   * [Kord] instance
   */
  val kord: Kord
    get() = space.kord

  /**
   * The guild that the [message] was sent in
   */
  suspend fun getGuild(): Guild?

  /**
   * The message channel that the [message] was sent in.
   */
  suspend fun getChannel(): MessageChannel

  /**
   * Replies to [message]
   *
   * @param content Message content
   */
  suspend fun reply(content: String, builder: MessageCreateBuilder.() -> Unit = {}): Message

  /**
   * Replies to [message] with an embed
   *
   * @param builder Embed builder
   */
  suspend fun reply(builder: EmbedBuilder.() -> Unit): Message
}
