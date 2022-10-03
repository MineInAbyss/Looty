package com.mineinabyss.looty


import com.mineinabyss.geary.addon.autoscan
import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.idofront.config.IdofrontConfig
import com.mineinabyss.idofront.config.config
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.service
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.systems.*
import org.bukkit.plugin.java.JavaPlugin

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: LootyPlugin by lazy { JavaPlugin.getPlugin(LootyPlugin::class.java) }

class LootyPlugin : JavaPlugin() {
    lateinit var config: IdofrontConfig<LootyConfig>
    override fun onLoad() {
        Platforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        config = config("config") { fromPluginPath(loadDefault = true) }

        //Reset to avoid duplicates and clear mixes that have been removed
        looty.server.potionBrewer.resetPotionMixes()
        gearyAddon {
            autoscan("com.mineinabyss") {
                all()
            }

            service<SerializablePrefabItemService>(LootySerializablePrefabItemService)

            LootyCommands() //Register commands
        }
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
//        Engine.forEach<ChildItemCache> { it.clear() }
    }
}
