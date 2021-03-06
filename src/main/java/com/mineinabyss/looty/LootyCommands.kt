package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.helpers.listComponents
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.config.LootyTypes
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
                    Engine.forEach<ChildItemCache> {
                        it.clear()
                    }

                    LootyConfig.reload(sender)

                    ItemTrackerSystem.tick()
                }
            }
            "item" {
                val type by optionArg(options = LootyTypes.types) {
                    parseErrorMessage = { "No such entity: $passed" }
                }

                playerAction {
//                    val lootyEntity = LootyTypes[type].instantiate()
                    player.inventory.addItem(LootyTypes[type].instantiateItemStack())
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
                        sender.info(player.get<ChildItemCache>()?.get(player.inventory.heldItemSlot)?.listComponents())
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
                    LootyTypes.types.filter { it.startsWith(args[1].toLowerCase()) }
                }
                else -> emptyList()
            }
            else -> emptyList()
        }
    }
}
