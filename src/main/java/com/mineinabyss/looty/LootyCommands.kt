package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.components.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.helpers.listComponents
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

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
                val type by optionArg(options = PrefabManager.keys.filter { it.plugin == looty.name }.map { it.name }) {
                    parseErrorMessage = { "No such entity: $passed" }
                }

                playerAction {
                    geary(player).addLooty(PrefabManager[PrefabKey(looty.name, type)] ?: return@playerAction)
                }
            }

            "debug" {
                "pdc"{
                    playerAction {
                        sender.info(player.inventory.itemInMainHand.itemMeta!!.persistentDataContainer.keys)
                    }
                }
                "components"{
                    playerAction {
                        sender.info(geary(player).get<ChildItemCache>()?.get(player.inventory.heldItemSlot)?.listComponents())
                    }
                    //TODO print static and serialized on separate lines
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
        if(command.name != "looty") return emptyList()
        return when(args.size) {
            2 -> when(args[0]) {
                "item" -> {
                    PrefabManager.keys.filter { it.plugin == looty.name }.map { it.name }.filter { it.startsWith(args[1].toLowerCase()) }
                }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
