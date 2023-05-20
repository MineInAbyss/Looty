package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.papermc.tracking.items.itemTracking
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

@AutoScan
class ItemRecipeSystem : GearyListener(), Listener {
    private val TargetScope.recipes by onSet<SetRecipes>()
    private val TargetScope.prefabKey by onSet<PrefabKey>()
    private val registeredRecipes = mutableSetOf<NamespacedKey>()
    private val discoveredRecipes = mutableSetOf<NamespacedKey>()

    @Handler
    fun TargetScope.onAdded() {
        val result: ItemStack? = recipes.result?.toItemStackOrNull()
            ?: itemTracking.provider.serializePrefabToItemStack(prefabKey)

        if (result == null) {
            looty.plugin.logger.warning("Recipe ${prefabKey.key} is missing result item")
            return
        }

        recipes.removeRecipes.forEach {
            Bukkit.removeRecipe(NamespacedKey.fromString(it)!!)
        }

        recipes.recipes.forEachIndexed { i, recipe ->
            val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")
            registeredRecipes += key
            // Register recipe only if not present
            Bukkit.getRecipe(key) ?: recipe.toRecipe(key, result, recipes.group).register()
            if (recipes.discoverRecipes) discoveredRecipes += key
        }
        entity.remove<SetRecipes>()
    }

    @EventHandler
    fun PlayerJoinEvent.showRecipesOnJoin() {
        player.discoverRecipes(discoveredRecipes)
    }
}
