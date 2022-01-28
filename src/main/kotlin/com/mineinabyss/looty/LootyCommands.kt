package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.helpers.listComponents
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.error
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery
import com.mineinabyss.looty.ecs.queries.LootyTypeQuery.key
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.tracking.toGearyOrNull
import com.okkero.skedule.schedule
import kotlinx.serialization.PolymorphicSerializer
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

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

                    ItemTrackerSystem.doTick()
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

                    player.inventory.setItem(slot, LootyFactory.createFromPrefab(PrefabKey.of(type)))
                    LootyFactory.loadFromPlayerInventory(
                        context = PlayerInventoryContext(player, slot)
                    )
                }
            }

            "debug" {
                "stone" {
                    playerAction {
                        player.inventory.itemInMainHand.toGearyOrNull(player)?.get<ItemStack>()?.type = Material.STONE
                    }
                }
                "reference" {
                    playerAction {
                        val item = player.inventory.itemInMainHand
                        CraftItemStack.asNMSCopy(item).asBukkitMirror().type = Material.STONE
                    }
                }
                "swap" {
                    val length by intArg()
                    playerAction {
                        val item = (player.inventory.itemInMainHand as CraftItemStack).handle
                        looty.schedule {
                            waitFor(length.toLong())
                            item.count += 1
                        }
                    }
                }
                "pdc"{
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.itemMeta!!.persistentDataContainer.keys)
                    }
                }
                "components"{
                    playerAction {
                        sender.info(
                            player.inventory.itemInMainHand.toGearyOrNull(player)?.listComponents()
                        )
                    }
                    //TODO print static and serialized on separate lines
                }
                "component" {
                    "add" {
                        action {
                            val player = sender as? Player ?: return@action
                            runCatching {
                                Formats.jsonFormat.decodeFromString(
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
                                Formats.getClassFor(name)
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