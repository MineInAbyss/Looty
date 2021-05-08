package com.mineinabyss.looty

import com.mineinabyss.geary.minecraft.dsl.attachToGeary
import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
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

class Looty : JavaPlugin() {
    val relicsDir = File(dataFolder, "relics")

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

        attachToGeary {
            systems(
                ItemTrackerSystem,
                ScreamingSystem,
                CooldownDisplaySystem,
                ItemRecipeSystem(),
            )

            autoscanActions()
            autoscanComponents()

            bukkitEntityAccess {
                onEntityRegister<Player> {
                    set(ChildItemCache())
                    lootyRefresh()
                }

                onEntityUnregister<Player> {
                    with<ChildItemCache> {
                        lootyRefresh()
                        it.clear()
                    }
                }
            }
            loadPrefabs(relicsDir)
        }
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
//        Engine.forEach<ChildItemCache> { it.clear() }
    }
}
