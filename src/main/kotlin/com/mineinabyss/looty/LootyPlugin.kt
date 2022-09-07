package com.mineinabyss.looty


import com.mineinabyss.geary.addon.GearyLoadPhase
import com.mineinabyss.geary.addon.autoscan
import com.mineinabyss.geary.papermc.dsl.gearyAddon
import com.mineinabyss.idofront.platforms.IdofrontPlatforms
import com.mineinabyss.idofront.plugin.registerService
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.looty.ecs.systems.*
import com.mineinabyss.looty.packgen.ResourcepackGeneration
import org.bukkit.plugin.java.JavaPlugin

/** Gets [Geary] via Bukkit once, then sends that reference back afterwards */
val looty: LootyPlugin by lazy { JavaPlugin.getPlugin(LootyPlugin::class.java) }

class LootyPlugin : JavaPlugin() {
    override fun onLoad() {
        IdofrontPlatforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        saveDefaultConfig()
        reloadConfig()

        //Reset to avoid duplicates and clear mixes that have been removed
        looty.server.potionBrewer.resetPotionMixes()

        registerService<SerializablePrefabItemService>(LootySerializablePrefabItemService)

        LootyCommands() //Register commands

        gearyAddon {
            autoscan("com.mineinabyss") {
                all()
            }
            startup {
                GearyLoadPhase.ENABLE {
                    val generation = ResourcepackGeneration()
                    generation.generateDefaultAssets()
                    generation.generateLootyItemAssets()
                }
            }
        }
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
//        Engine.forEach<ChildItemCache> { it.clear() }
    }
}
