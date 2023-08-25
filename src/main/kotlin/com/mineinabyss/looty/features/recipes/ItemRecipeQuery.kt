package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.query.Query
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class ItemRecipeQuery : Query() {
    private val TargetScope.recipes by get<SetRecipes>()
    private val TargetScope.prefabKey by get<PrefabKey>()

    fun TargetScope.registerRecipes(): Set<NamespacedKey> {
        val discoveredRecipes = mutableSetOf<NamespacedKey>()
        val result: ItemStack? = recipes.result?.toItemStackOrNull() ?: gearyItems.createItem(prefabKey)

        if (result == null) {
            looty.plugin.logger.warning("Recipe ${prefabKey.key} is missing result item")
            return emptySet()
        }

        recipes.removeRecipes.forEach {
            runCatching {
                Bukkit.removeRecipe(NamespacedKey.fromString(it)!!)
            }.onFailure { it.printStackTrace() }
        }

        recipes.recipes.forEachIndexed { i, recipe ->
            runCatching {
                val key = NamespacedKey(prefabKey.namespace, "${prefabKey.key}$i")
                // Register recipe only if not present
                Bukkit.getRecipe(key) ?: recipe.toRecipe(key, result, recipes.group).register()
                if (recipes.discoverRecipes) discoveredRecipes += key
            }.onFailure {
                looty.plugin.logger.warning("Failed to register recipe ${prefabKey.key} #$i, ${it.message}")
            }
        }
        return discoveredRecipes
    }
}
