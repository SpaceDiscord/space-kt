package dimensional.space.api.errors

class ResolverNotRegistered(type: Class<*>) : IllegalStateException("No resolvers registered for `${type.simpleName}`")
