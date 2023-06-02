package com.mineinabyss.looty


import com.mineinabyss.geary.addons.GearyPhase
import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.platforms.Platforms
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.idofront.plugin.service
import com.mineinabyss.idofront.serialization.SerializablePrefabItemService
import com.mineinabyss.looty.config.LootyModule
import com.mineinabyss.looty.features.backpack.BackpackListener
import com.mineinabyss.looty.features.recipes.ItemRecipeQuery
import com.mineinabyss.looty.features.recipes.ItemRecipes
import com.mineinabyss.looty.features.wearables.WearableItemSystem
import org.bukkit.plugin.java.JavaPlugin

class LootyPlugin : JavaPlugin() {
    override fun onLoad() {
        Platforms.load(this, "mineinabyss")
    }

    override fun onEnable() {
        DI.add(LootyModule(this))

        //Reset to avoid duplicates and clear mixes that have been removed
        server.potionBrewer.resetPotionMixes()

        geary {
            autoscan(classLoader, "com.mineinabyss.looty") {
                all()
            }
            install(ItemRecipes)
        }
        service<SerializablePrefabItemService>(LootySerializablePrefabItemService())
        LootyCommands() //Register commands

        listeners(
            WearableItemSystem(),
            BackpackListener()
        )
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
    }
}
