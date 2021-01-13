package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.minecraft.dsl.attachToGeary
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.looty.config.LootyAddon
import com.mineinabyss.looty.config.LootyTypes
import com.mineinabyss.looty.config.registerAddonWithLooty
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.events.LootyEventListener
import com.mineinabyss.looty.ecs.systems.*
import kotlinx.serialization.InternalSerializationApi
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.time.ExperimentalTime

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: Looty by lazy { JavaPlugin.getPlugin(Looty::class.java) }

class Looty : JavaPlugin(), LootyAddon {
    override val relicsDir = File(dataFolder, "relics")

    @InternalSerializationApi
    @ExperimentalCommandDSL
    @ExperimentalTime
    override fun onEnable() {
        logger.info("On enable has been called")
        saveDefaultConfig()
        reloadConfig()

        //Register commands
        LootyCommands()

        registerEvents(
            InventoryTrackingListener,
            LootyEventListener
        )

        attachToGeary(types = LootyTypes) {
            systems(
                ItemTrackerSystem,
                ScreamingSystem,
                PotionEffectSystem,
                CooldownDisplaySystem,
            )

            autoscanActions()
            autoscanComponents()

            bukkitEntityAccess {
                onEntityRegister<Player> { player ->
                    add(ChildItemCache(player))
                }

                onEntityUnregister<Player> { gearyPlayer, player ->
                    gearyPlayer.with<ChildItemCache> {
                        it.reevaluate(player.inventory)
                        it.clear()
                    }
                }
            }
        }

        registerAddonWithLooty()
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
