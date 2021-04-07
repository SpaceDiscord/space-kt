package dimensional.space.internal.conversion

import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.params.Converter
import dimensional.space.api.errors.ResolverNotRegistered

class ConverterManager {

  /**
   * Registry of all resolvers.
   */
  val registry = hashMapOf<Class<*>, Converter<*>>()

  operator fun get(klass: Class<*>): Converter<*> =
    registry[klass] ?: throw ResolverNotRegistered(klass)

  fun <T : Any> addConverter(klass: Class<T>, converter: Converter<T>) {
    registry[klass] = converter
  }

  /**
   * Adds a converter for [T]
   *
   * @param convert Resolver block
   */
  inline fun <reified T : Any> addConverter(noinline convert: suspend (CommandContext, String) -> T?): ConverterManager {
    addConverter(T::class.java, object : Converter<T> {
      override suspend fun convert(ctx: CommandContext, content: String): T? =
        content.runCatching { convert(ctx, this) }.getOrNull()
    })

    return this
  }
}
