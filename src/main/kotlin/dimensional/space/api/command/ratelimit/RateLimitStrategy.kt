package dimensional.space.api.command.ratelimit

import dimensional.space.api.command.Command
import dimensional.space.api.command.CommandContext

interface RateLimitStrategy {
  /**
   * Checks whether the entity associated with the provided ID is being rate-limited.
   * When BucketType is `Guild` and the command was invoked in a private context, this method won't be called.
   *
   * @param id The ID of the entity.
   *   [BucketType.User]: it will be the author's id
   *   [BucketType.Channel]: it will be ID of the channel the command was invoked in
   *   [BucketType.Guild]: if ran in a guild, it'll be the guild id
   *   [BucketType.Global]: -1
   *
   * @param command
   *   The command being invoked
   *
   * @param rateLimit
   *   Cooldown of [command]
   *
   * @return true, if the entity associated with [id] is being rate-limited
   */
  suspend fun isLimited(id: Long, command: Command, rateLimit: RateLimit): Boolean

  /**
   * Gets the remaining time of the cooldown in milliseconds.
   * This may either return 0L, or return null if a rate-limit isn't active, however this
   * should not happen as `locked` should be called prior to this.
   *
   * @param id
   *   Entity ID
   *
   * @param command
   *   Command that was invoked
   *
   * @param rateLimit
   *   Rate-limit information of [command]
   *
   * @return Time remaining in milliseconds, or null if no rate-limit is active
   */
  suspend fun getRemaining(id: Long, command: Command, rateLimit: RateLimit): Long?

  /**
   * Adds a rate-limit for the given entity ID. It is up to you whether this passively, or actively removes expired rate-limits.
   * When BucketType is `GUILD` and the command was invoked in a private context, this method won't be called.
   *
   * @param id
   *   Entity ID, can be the channel, guild, or user id, or -1 if global
   *
   * @param command
   *   Command that was invoked
   *
   * @param rateLimit
   *   Cooldown of the [command]
   */
  suspend fun consume(id: Long, command: Command, rateLimit: RateLimit)

  companion object {
    /**
     * Returns the entity id that corresponds with the provided [bucketType]
     *
     * @param ctx The current command context
     * @param bucketType Bucket type
     */
    fun getEntityId(ctx: CommandContext, bucketType: BucketType): Long = when {
      bucketType == BucketType.User ->
        ctx.message.author!!.id.value

      bucketType == BucketType.Guild && ctx.message.data.guildId.value != null ->
        ctx.message.data.guildId.value!!.value

      bucketType == BucketType.Channel ->
        ctx.message.channelId.value

      else -> -1L
    }
  }
}
