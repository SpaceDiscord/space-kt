package dimensional.space.api.command

class CommandParameters(private val map: HashMap<String, Any?>) {
  /**
   */
  fun contains(name: String): Boolean {
    return map.containsKey(name) && map[name] != null
  }

  /**
   */
  fun <T> get(name: String): T {
    @Suppress("unchecked_cast")
    return map[name] as T
  }

  /**
   */
  fun <T> getOrNull(name: String): T? = name
    .runCatching { get<T>(this) }
    .getOrNull()
}
