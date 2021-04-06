package dimensional.space.api.errors

import kotlin.reflect.KClass

class ResolverNotRegistered(type: KClass<*>) : IllegalStateException("No resolvers registered for `${type.simpleName}`")
