package com.mineinabyss.looty

import com.mineinabyss.idofront.commands.execution.ExperimentalCommandDSL
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.looty.config.LootyAddon
import com.mineinabyss.looty.config.registerAddonWithLooty
import com.mineinabyss.looty.ecs.components.events.LootyEventListener
import com.mineinabyss.looty.ecs.systems.InventoryTrackingListener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import kotlin.time.ExperimentalTime

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: Looty by lazy { JavaPlugin.getPlugin(Looty::class.java) }

class Looty : JavaPlugin(), LootyAddon {
    override val relicsDir = File(dataFolder, "relics")

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

        attachToGeary()

        registerAddonWithLooty()
    }

    override fun onDisable() {
        super.onDisable()
        logger.info("onDisable has been invoked!")
        server.scheduler.cancelTasks(this)
    }
}
