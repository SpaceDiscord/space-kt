package dimensional.space.internal.util

import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.on
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.min

typealias MessageCondition = suspend (Message) -> Boolean

/**
 * Waits for a message that matches [condition] to be set.
 *
 * @param timeout Time to wait, or null to wait indefinitely
 * @param condition Condition the message has to match
 */
suspend fun MessageChannel.awaitMessage(timeout: Long?, condition: MessageCondition = { true }): Message? {
  val event = kord.waitFor<MessageCreateEvent>(timeout) {
    message.channelId == id && condition(message)
  }

  return event?.message
}

/**
 * Waits for [max] number of messages that match [condition] to be sent
 *
 * @param timeout Time to wait, or null to wait indefinitely
 * @param max Total number of messages to wait for
 * @param condition Condition each message has to match
 */
suspend fun MessageChannel.awaitMessages(
  timeout: Long,
  max: Int = 1,
  condition: MessageCondition = { true }
): List<Message> {
  val messages = mutableListOf<Message>()

  /* cancelled */
  val deleted = kord.on<MessageDeleteEvent> {
    messages.remove(message)
  }

  /* wait for messages */
  withTimeoutOrNull(timeout) {
    while (messages.size < max) {
      awaitMessage(min(0, timeout / max), condition)
        ?.let { messages.add(it) }
    }
  }

  deleted.cancel()

  return messages
}
