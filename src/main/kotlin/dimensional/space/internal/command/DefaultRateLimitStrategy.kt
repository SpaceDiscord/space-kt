package dimensional.space.internal.command

import dimensional.space.api.command.Command
import dimensional.space.api.command.ratelimit.RateLimit
import dimensional.space.api.command.ratelimit.RateLimitStrategy

class DefaultRateLimitStrategy : RateLimitStrategy {
  override suspend fun consume(id: Long, command: Command, rateLimit: RateLimit) {
    TODO("Not yet implemented")
  }

  override suspend fun isLimited(id: Long, command: Command, rateLimit: RateLimit): Boolean {
    TODO("Not yet implemented")
  }

  override suspend fun getRemaining(id: Long, command: Command, rateLimit: RateLimit): Long? {
    TODO("Not yet implemented")
  }
}