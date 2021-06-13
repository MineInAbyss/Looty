package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.GearyComponent
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.helpers.listComponents
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.components.getPrefabsFor
import com.mineinabyss.geary.minecraft.components.of
import com.mineinabyss.idofront.commands.arguments.intArg
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.arguments.stringArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import com.mineinabyss.looty.tracking.gearyOrNull
import com.okkero.skedule.schedule
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.SerializationException
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@ExperimentalCommandDSL
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

                    ItemTrackerSystem.tick()
                }
            }
            "item" {
                //TODO more efficient way of finding the right types
                val type by optionArg(options = PrefabManager.getPrefabsFor(looty).map { it.name }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }

                playerAction {
                    LootyFactory.createFromPrefab(
                        parent = geary(player),
                        prefab = PrefabManager[PrefabKey.of(looty, type)] ?: return@playerAction,
                        context = PlayerInventoryContext(player, player.inventory.firstEmpty())
                    )
                }
            }

            "debug" {
                "stone" {
                    playerAction {
                        gearyOrNull(player.inventory.itemInMainHand)?.get<ItemStack>()?.type = Material.STONE
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
                            gearyOrNull(player.inventory.itemInMainHand)?.listComponents()
                        )
                    }
                    //TODO print static and serialized on separate lines
                }
                "component" {
                    "add" {
                        action {
                            val player = if (sender is Player) sender as Player else return@action
                            val json = arguments.joinToString(" ")
                            val item = gearyOrNull(player.inventory.itemInMainHand) ?: return@action

                            try {
                                val component = Formats.jsonFormat.decodeFromString(PolymorphicSerializer(GearyComponent::class), json)
                                item.set(component, component::class)
                            } catch (e: SerializationException) {
                                player.info(e.message)
                            }
                        }
                    }

                    "remove" {
                        val name by stringArg()
                        playerAction {
                            try {
                                val kClass = Formats.getClassFor(name)
                                val item = gearyOrNull(player.inventory.itemInMainHand) ?: return@playerAction
                                item.remove(kClass)
                            } catch (e: IllegalStateException) {
                                player.info(e.message)
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
                    PrefabManager.getPrefabsFor(looty).map { it.name }.filter { it.startsWith(args[1].lowercase()) }
                }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
