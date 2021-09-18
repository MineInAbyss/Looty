package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.idofront.recpies.register
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.DiscoverRecipe
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.RegisterRecipeComponent
import org.bukkit.NamespacedKey
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ItemRecipeSystem : TickingSystem(), Listener {
    private val QueryResult.recipes by get<RegisterRecipeComponent>()
    private val QueryResult.discoverRecipe by get <DiscoverRecipe>()
    private val QueryResult.type by get<LootyType>()
    private val QueryResult.prefabKey by get<PrefabKey>()
    private val registeredRecipes = mutableSetOf<NamespacedKey>()
    private val discoveredRecipes = mutableSetOf<NamespacedKey>()

    override fun QueryResult.tick() {
        val result = LootyFactory.createFromPrefab(prefabKey) ?: return

        recipes.wrapped.forEachIndexed { i, recipe ->
            @Suppress("DEPRECATION")
            val key = NamespacedKey(prefabKey.plugin, "${prefabKey.name}$i")
            registeredRecipes += key
            recipe.toCraftingRecipe(key, result).register()
            if (discoverRecipe.equals(false)) return
            discoveredRecipes += key
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
