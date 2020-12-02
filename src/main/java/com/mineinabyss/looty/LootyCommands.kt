package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.geary.minecraft.store.encodeComponents
import com.mineinabyss.idofront.commands.Command
import com.mineinabyss.idofront.commands.arguments.optionArg
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.commands.execution.IdofrontCommandExecutor
import com.mineinabyss.idofront.commands.extensions.actions.playerAction
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.PotionComponent
import com.mineinabyss.looty.ecs.components.Screaming

@ExperimentalCommandDSL
object LootyCommands : IdofrontCommandExecutor() {
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
                "registerself" {
                    playerAction {
                        Engine.entity {
                            addComponents(setOf(PlayerComponent(player.uniqueId), ChildItemCache(player)))
                        }
                    }
                }
                fun Command.components(vararg components: GearyComponent) {
                    playerAction {
                        player.inventory.itemInMainHand.apply {
                            itemMeta = itemMeta.apply {
                                persistentDataContainer.encodeComponents(components.toSet())
                            }
                        }
                    }
                }

                "screamingitem" {
                    components(Screaming())
                }
                "speeditem" {
                    components(PotionComponent("SPEED", 2))
                }
            }
        }
    }
}
