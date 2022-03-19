package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.api.engine.runSafely
import com.mineinabyss.geary.ecs.helpers.listComponents
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventorySlotContext
import com.mineinabyss.looty.ecs.components.itemcontexts.useWithLooty
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery.key
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.tracking.toGearyOrNull
import kotlinx.serialization.PolymorphicSerializer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

context(GearyMCContext)
class LootyCommands : IdofrontCommandExecutor(), TabCompleter {
    override val commands = commands(looty) {
        "looty" {
            "reload" {
                action {
                    // Re-register all items in every player's inventory
//                    Engine.forEach<ChildItemCache> {
//                        it.clear()
//                    }

                    LootyConfig.reload(sender)
                    runSafely {
                        ItemTrackerSystem.doTick()
                    }
                }
            }
            "item" {
                //TODO more efficient way of finding the right types
                val type by optionArg(options = LootyTypeQuery.map { it.key.toString() }) {
                    parseErrorMessage = { "No such item: $passed" }
                }

                playerAction {
                    val slot = player.inventory.firstEmpty()
                    if (slot == -1) {
                        player.error("No empty slots in inventory")
                        return@playerAction
                    }

                    val item = LootyFactory.createFromPrefab(PrefabKey.of(type))
                    item?.useWithLooty {
                        PlayerInventorySlotContext(player, slot).loadItem()
                    }
                    player.inventory.setItem(slot, item)
                }
            }

            "debug" {
                "stone" {
                    playerAction {
                        player.inventory.itemInMainHand.toGearyOrNull(player)?.get<ItemStack>()?.type = Material.STONE
                    }
                }
                "pdc"{
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.itemMeta!!.persistentDataContainer.keys)
                    }
                }
                "components"{
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.toGearyOrNull(player)?.listComponents())
                    }
                    //TODO print static and serialized on separate lines
                }
                "component" {
                    "add" {
                        action {
                            val player = sender as? Player ?: return@action
                            runCatching {
                                formats.jsonFormat.decodeFromString(
                                    PolymorphicSerializer(GearyComponent::class),
                                    arguments.joinToString(" ")
                                )
                            }.onSuccess {
                                player.inventory.itemInMainHand.toGearyOrNull(player)
                                    ?.set(it, it::class)
                            }.onFailure {
                                player.info(it.message)
                            }
                        }
                    }

                    "remove" {
                        val name by stringArg()
                        playerAction {
                            runCatching {
                                formats.getClassFor(name)
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
                            it.key.name.startsWith(arg) || it.key.key.startsWith(arg)
                        }
                        .map { it.key.toString() }
                }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
