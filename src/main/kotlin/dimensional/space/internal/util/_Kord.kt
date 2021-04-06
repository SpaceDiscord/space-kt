package dimensional.space.internal.util

import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull


/**
 * Flow containing all [User] objects in the cache.
 */
val Kord.users: Flow<User>
  get() = with(EntitySupplyStrategy.cache).users

/**
 * Waits for the first event [T] that matches the supplied [condition]
 *
 * @param timeout Time to wait, or null to wait indefinitely
 * @param condition Condition to use
 */
suspend inline fun <reified T> Kord.waitFor(
  timeout: Long?,
  noinline condition: suspend T.() -> Boolean = { true }
): T? {
  return if (timeout == null) {
    events.filterIsInstance<T>().firstOrNull(condition)
  } else {
    withTimeoutOrNull(timeout) {
      events.filterIsInstance<T>().firstOrNull(condition)
    }
  }
}
