package dimensional.space.internal.conversion

import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.args.Converter
import dimensional.space.api.errors.ResolverNotRegistered
import kotlin.reflect.KClass

class ConverterManager {

  /**
   * Registry of all resolvers.
   */
  val registry = hashMapOf<KClass<*>, Converter<*>>()

  operator fun get(klass: KClass<*>): Converter<*> =
    registry[klass] ?: throw ResolverNotRegistered(klass)

  operator fun invoke(block: ConverterManager.() -> Unit) =
    block()

  fun <T : Any> addConverter(klass: KClass<T>, converter: Converter<T>) {
    registry[klass] = converter
  }

  /**
   * Adds a converter for [T]
   *
   * @param convert Resolver block
   */
  inline fun <reified T : Any> addConverter(noinline convert: suspend (CommandContext, String) -> T?): ConverterManager {
    addConverter(T::class, object : Converter<T> {
      override suspend fun convert(ctx: CommandContext, content: String): T? =
        content.runCatching { convert(ctx, this) }.getOrNull()
    })

    return this
  }
}
