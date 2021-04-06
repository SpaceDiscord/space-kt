package dimensional.space.api.command.ratelimit

import java.util.concurrent.TimeUnit

data class RateLimit(
  val cooldown: Long,
  val uses: Int,
  val timeUnit: TimeUnit = TimeUnit.MILLISECONDS,
  val bucket: BucketType = BucketType.User
)
