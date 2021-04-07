package dimensional.space.api.command

import dev.kord.common.entity.Permission

data class CommandRestrictions(
  var developerOnly: Boolean = false,
  var guildOnly: Boolean = false,
  var userPerms: List<Permission> = emptyList(),
  var clientPerms: List<Permission> = emptyList()
)
