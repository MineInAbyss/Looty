package com.mineinabyss.looty.features.recipes

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

@AutoScan
class ItemRecipeSystem : RepeatingSystem(), Listener {
    private val TargetScope.recipes by get<RegisterRecipeComponent>()
    private val TargetScope.prefabKey by get<PrefabKey>()
    private val registeredRecipes = mutableSetOf<NamespacedKey>()
    private val discoveredRecipes = mutableSetOf<NamespacedKey>()

    override fun TargetScope.tick() {
        val result: ItemStack? = recipes.result?.toItemStackOrNull()
            ?: LootyFactory.createFromPrefab(this.prefabKey)

        if (result != null) {
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
            entity.remove<RegisterRecipeComponent>()
        } else looty.logger.warning("Recipe ${prefabKey.key} is missing result item")
    }

    @EventHandler
    fun PlayerJoinEvent.showRecipesOnJoin() {
        player.discoverRecipes(discoveredRecipes)
    }
}
