package dimensional.space.internal.conversion

import dimensional.space.api.command.CommandContext
import dimensional.space.api.command.params.ConversionData
import dimensional.space.api.command.params.ConversionStrategy
import dimensional.space.api.command.params.Parameter

class DefaultConversionStrategy(val converters: ConverterManager) : ConversionStrategy {
  override val name = "default"

  override suspend fun consume(ctx: CommandContext, data: ConversionData): HashMap<String, Any?> {
    /** get arguments delimited by [ConversionData.delimiter] */
    val delimited = data.input.takeUnless { data.delimiter != ' ' }
      ?: data.input.joinToString(" ").split(data.delimiter).toMutableList()

    /* create parser */
    val parser = Parser(ctx, data.delimiter, delimited)

    /* start conversion */
    val converted = hashMapOf<String, Any?>()
    for (p in data.parameters) {
      /* parse the parameter with the next available argument */
      val parsed = parser.parse(p)

      /* check if the parsed value is usable */
      val isUsable = parsed != null || (p.isNullable && !p.isOptional) || (p.isTentative && p.isNullable)
      if (isUsable) {
        /*
          This will only place the argument into the map if its parsed value is null or
          if the parameter requires a value (i.e. marked nullable)

          Commands marked optional already have a parameter so they don't need user-provided values
          unless the argument was successfully converted for that parameter.
         */

        converted[p.name] = parsed
      }
    }

    return converted
  }

  inner class Parser(private val ctx: CommandContext, private val delimiter: Char, input: List<String>) {
    private val delimiterStr = delimiter.toString()
    private var args = input.toMutableList()

    /**
     * Parses the next argument in [args]
     */
    suspend fun <T> parse(param: Parameter<T>): T? {
      val converter = converters[param.type]

      /* convert next argument */
      val (argument, original) = getNextArgument(param.isGreedy)
      val converted = argument.takeUnless { it.isEmpty() }?.let {
        converter.convert(ctx, it)
      }

      /* if conversion failed, try substituting */
      val canSubstitute = param.isTentative || param.isNullable || (param.isOptional && argument.isEmpty())
      if (canSubstitute && converted == null) {
        /* restore original arguments */
        restore(original)
      }

      @Suppress("unchecked_cast")
      return converted as T?
    }

    private fun take(amount: Int) =
      args.take(amount).onEach { args.removeAt(0) }

    private fun restore(argList: List<String>) =
      args.addAll(0, argList)

    /**
     * @returns a Pair of the parsed argument, and the original args.
     */
    private fun getNextArgument(greedy: Boolean): Pair<String, List<String>> {
      val (argument, original) = when {
        args.isEmpty() ->
          Pair("", emptyList())

        greedy ->
          take(args.size).let { Pair(it.joinToString(delimiterStr), it) }

        quotes.any { (o) -> args[0].startsWith(o) } && delimiter == ' ' ->
          parseQuoted()

        else ->
          take(1).let { Pair(it.joinToString(delimiterStr), it) }
      }

      var unquoted = argument.trim()
      if (!greedy) {
        unquoted = unquoted.removeSurrounding("\"")
      }

      return Pair(unquoted, original)
    }

    private fun parseQuoted(): Pair<String, List<String>> {
      val iter = args.joinToString(delimiterStr).iterator()
      val original = StringBuilder()
      val argument = StringBuilder("\"")

      /* state */
      var quoting = false
      var quote: Char? = null
      var escaping = false

      loop@ while (iter.hasNext()) {
        val char = iter.nextChar()
        original.append(char)

        when {
          escaping -> {
            argument.append(char)
            escaping = false
          }

          !quoting && quotes.containsKey(char) -> {
            quoting = true
            quote = quotes[char]
          }

          quoting && char == quote -> {
            quoting = false
            quote = null
          }

          !quoting && char == delimiter -> {
            if (argument.isEmpty()) {
              continue@loop
            }

            break@loop
          }

          else -> argument.append(char)
        }
      }

      argument.append('"')
      val remainingArgs = buildString {
        iter.forEachRemaining { append(it) }
      }

      args = remainingArgs.split(delimiter).toMutableList()
      return Pair(argument.toString(), original.split(delimiter))
    }
  }

  companion object {
    val quotes = mapOf(
      '«' to '»',
      '‹' to '›',
      '『' to '』',
      '"' to '"',
      '“' to '”',
      '‛' to '’',
      '`' to '`',
      '\'' to '\'',
      '‘' to '’',
      '「' to '」',
      '｢' to '｣',
      '《' to '》',
      '〈' to '〉',
      '＇' to '＇',
      '〝' to '〞',
      '＂' to '＂',
    )
  }
}
