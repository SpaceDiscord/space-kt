package dimensional.space.api.event

import dev.kord.core.Kord
import dimensional.space.api.Space
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

interface SpaceEvent : CoroutineScope {
  override val coroutineContext: CoroutineContext
    get() = space.kord.coroutineContext

  /**
   * The current Space instance
   */
  val space: Space

  /**
   * The current Kord instance used in [space]
   */
  val kord: Kord
    get() = space.kord
}
