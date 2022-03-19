package com.mineinabyss.looty


import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.registerEvents
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.looty.ecs.systems.*
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: LootyPlugin by lazy { JavaPlugin.getPlugin(LootyPlugin::class.java) }

class LootyPlugin : JavaPlugin() {
    val itemsDir = File(dataFolder, "items")

    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()
        reloadConfig()

        registerEvents(
            InventoryTrackingListener,
        )

        registerService<SerializablePrefabItemService>(LootySerializablePrefabItemService)

        gearyAddon {
            autoScanAll()
            loadPrefabs(itemsDir)
            LootyCommands() //Register commands
        }
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
//        Engine.forEach<ChildItemCache> { it.clear() }
    }
}
