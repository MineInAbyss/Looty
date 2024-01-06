package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.papermc.tracking.items.gearyItems
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.accessors.Pointer
import com.mineinabyss.geary.systems.query.GearyQuery
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.idofront.serialization.recipes.SerializableRecipeIngredients
import com.mineinabyss.looty.config.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack

class ItemRecipeQuery : GearyQuery() {
    val Pointer.recipes by get<SetRecipes>()
    val Pointer.prefabKey by get<PrefabKey>()

    fun registerRecipes(): Set<NamespacedKey> {
        val discoveredRecipes = mutableSetOf<NamespacedKey>()

        forEach { pointer ->
            val result: ItemStack? = runCatching { pointer.recipes.result?.toItemStackOrNull() ?: gearyItems.createItem(pointer.prefabKey) }
                .getOrNull()

            if (result == null) {
                looty.plugin.logger.warning("Recipe ${pointer.prefabKey.key} is missing result item")
                return@forEach
            }

            pointer.recipes.removeRecipes.forEach {
                runCatching {
                    Bukkit.removeRecipe(NamespacedKey.fromString(it)!!)
                }.onFailure { it.printStackTrace() }
            }

            pointer.recipes.recipes.forEachIndexed { i, recipe ->
                runCatching {
                    val key = NamespacedKey(pointer.prefabKey.namespace, "${pointer.prefabKey.key}$i")
                    // Register recipe only if not present
                    Bukkit.getRecipe(key) ?: recipe.toRecipe(key, result, pointer.recipes.group).register()
                    if (pointer.recipes.discoverRecipes) discoveredRecipes += key
                }.onFailure {
                    looty.plugin.logger.warning("Failed to register recipe ${pointer.prefabKey.key} #$i, ${it.message}")
                }
            }
        }
        return discoveredRecipes
    }
}
