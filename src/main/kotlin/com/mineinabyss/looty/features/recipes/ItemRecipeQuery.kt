package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.query.CachedQueryRunner
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class ItemRecipeQuery : GearyQuery() {
    val recipes by get<SetRecipes>()
    val prefabKey by get<PrefabKey>()
}

fun CachedQueryRunner<ItemRecipeQuery>.registerRecipes(): Set<NamespacedKey> {
    val discoveredRecipes = mutableSetOf<NamespacedKey>()

    forEach {
        val result: ItemStack? = runCatching {
            recipes.result?.toItemStackOrNull() ?: gearyItems.createItem(prefabKey)
        }
            .getOrNull()

        if (result == null) {
            looty.logger.w("Recipe ${prefabKey.key} is missing result item")
            return@forEach
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
                looty.logger.w("Failed to register recipe ${prefabKey.key} #$i, ${it.message}")
            }
        }
    }
    return discoveredRecipes
}
