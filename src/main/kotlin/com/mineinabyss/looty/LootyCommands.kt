package com.mineinabyss.looty

import com.mineinabyss.geary.helpers.listComponents
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery.key
import com.mineinabyss.looty.tracking.toGearyOrNull
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.inventory.ItemStack

class LootyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(looty) {
        "looty" {
            "reload" {
                action {
                    looty.config = config("config") { looty.fromPluginPath(loadDefault = true) }
                }
            }
            "item" {
                //TODO more efficient way of finding the right types
                val type by optionArg(options = LootyTypeQuery.map { it.key.toString() }) {
                    parseErrorMessage = { "No such item: $passed" }
                }
                val amount by intArg { default = 1 }

                playerAction {
                    val slot = player.inventory.firstEmpty()
                    if (slot == -1) {
                        player.error("No empty slots in inventory")
                        return@playerAction
                    }

                    val item = LootyFactory.createFromPrefab(PrefabKey.of(type))
                    if (item == null) {
                        player.error("$type exists but is not an item.")
                        return@playerAction
                    }
                    item.amount = amount
                    player.inventory.addItem(item)
                }
            }

            "debug" {
                "stone" {
                    playerAction {
                        player.inventory.itemInMainHand.toGearyOrNull(player)?.get<ItemStack>()?.type = Material.STONE
                    }
                }
                "pdc" {
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.itemMeta!!.persistentDataContainer.keys)
                    }
                }
                "components" {
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.toGearyOrNull(player)?.listComponents())
                    }
                    //TODO print static and serialized on separate lines
                }
                "component" {
//                    "add" {
//                        action {
//                            val player = sender as? Player ?: return@action
//                            runCatching {
//                                globalContextMC.formats.getFormat("json").decodeFromString(
//                                    PolymorphicSerializer(GearyComponent::class),
//                                    arguments.joinToString(" ")
//                                )
//                            }.onSuccess {
//                                player.inventory.itemInMainHand.toGearyOrNull(player)
//                                    ?.set(it, it::class)
//                            }.onFailure {
//                                player.info(it.message)
//                            }
//                        }
//                    }

                    "remove" {
                        val name by stringArg()
                        playerAction {
                            runCatching {
                                globalContextMC.serializers.getClassFor(name)
                            }.onSuccess {
                                player.inventory.itemInMainHand.toGearyOrNull(player)
                                    ?.remove(it)
                            }.onFailure {
                                player.info(it.message)
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (command.name != "looty") return emptyList()
        return when (args.size) {
            2 -> when (args[0]) {
                "item" -> {
                    LootyTypeQuery
                        .filter {
                            val arg = args[1].lowercase()
                            it.key.key.startsWith(arg) || it.key.full.startsWith(arg)
                        }
                        .map { it.key.toString() }
                }
                else -> emptyList()
            }
            3 -> when (args[0]) {
                "item" -> listOf("1", "64")
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
