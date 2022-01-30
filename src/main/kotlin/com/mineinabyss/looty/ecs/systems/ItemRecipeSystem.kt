package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.prefabs.PrefabKey
import com.mineinabyss.idofront.recipes.register
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.RegisterRecipeComponent
import com.mineinabyss.looty.looty
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

@AutoScan
class ItemRecipeSystem : TickingSystem(), Listener {
    private val TargetScope.recipes by get<RegisterRecipeComponent>()
    private val TargetScope.prefabKey by get<PrefabKey>()
    private val registeredRecipes = mutableSetOf<NamespacedKey>()
    private val discoveredRecipes = mutableSetOf<NamespacedKey>()

    override suspend fun TargetScope.tick() {
        val result: ItemStack = if (entity.has<LootyType>()) {
            LootyFactory.createFromPrefab(prefabKey) ?: return
        } else {
            recipes.result?.toItemStack() ?: run {
                looty.logger.warning("Recipe ${prefabKey.name} is missing result item")
                return
            }
        }

        recipes.removeRecipes.forEach {
            Bukkit.removeRecipe(NamespacedKey.fromString(it)!!)
        }

        recipes.recipes.forEachIndexed { i, recipe ->
            @Suppress("DEPRECATION")
            val key = NamespacedKey(prefabKey.namespace, "${prefabKey.name}$i")
            registeredRecipes += key
            recipe.toRecipe(key, result, recipes.group).register()
            if (recipes.discoverRecipes) discoveredRecipes += key
        }
        entity.remove<RegisterRecipeComponent>()
    }

    //TODO these recipes are broken ingame, clicking will put the item type in regardless of NBT
    // Probably best to just show recipes in an online wiki instead.
    @EventHandler
    fun PlayerJoinEvent.showRecipesOnJoin() {
        player.discoverRecipes(discoveredRecipes)
    }
}
