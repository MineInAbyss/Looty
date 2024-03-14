package com.mineinabyss.looty


import com.mineinabyss.geary.autoscan.autoscan
import com.mineinabyss.geary.modules.geary
import com.mineinabyss.idofront.di.DI
import com.mineinabyss.idofront.plugin.listeners
import com.mineinabyss.looty.config.LootyModule
import com.mineinabyss.looty.features.backpack.BackpackListener
import com.mineinabyss.looty.features.food.FoodConsumptionListener
import com.mineinabyss.looty.features.holdsentity.SpawnHeldPrefabSystem
import com.mineinabyss.looty.features.nointeraction.DisableItemInteractionsBukkitListener
import com.mineinabyss.looty.features.recipes.ItemRecipes
import com.mineinabyss.looty.features.recipes.createPotionMixRecipeSystem
import com.mineinabyss.looty.features.wearables.WearableItemSystem
import org.bukkit.plugin.java.JavaPlugin

class LootyPlugin : JavaPlugin() {
    override fun onLoad() {
        DI.add(LootyModule(this))

        geary {
            autoscan(classLoader, "com.mineinabyss.looty") {
                all()
            }
            install(ItemRecipes)
        }

        geary.run {
            createPotionMixRecipeSystem()
        }
    }

    override fun onEnable() {
        LootyCommands() //Register commands

        listeners(
            WearableItemSystem(),
            BackpackListener(),
            FoodConsumptionListener(),
            SpawnHeldPrefabSystem(),
            DisableItemInteractionsBukkitListener()
        )
    }

    override fun onDisable() {
        super.onDisable()
        server.scheduler.cancelTasks(this)
    }
}
