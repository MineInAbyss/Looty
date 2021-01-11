package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.helpers.listComponents
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.idofront.messaging.info
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.ecs.components.ChildItemCache

@ExperimentalCommandDSL
class LootyCommands : IdofrontCommandExecutor() {
    override val commands = commands(looty) {
        "looty" {
            "reload" {
                action {
                    LootyConfig.reload(sender)
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
                "registerself" {
                    playerAction {
                        Engine.entity {
                            addComponents(setOf(PlayerComponent(player.uniqueId), ChildItemCache(player)))
                        }
                    }
                }
            }
        }
    }
}
