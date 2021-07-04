package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.RegisterRecipeComponent
import com.mineinabyss.looty.encodeComponentsTo
import org.bukkit.NamespacedKey

class ItemRecipeSystem : TickingSystem() {
    private val QueryResult.recipes by get<RegisterRecipeComponent>()
    private val QueryResult.type by get<LootyType>()
    private val QueryResult.prefabKey by get<PrefabKey>()

    override fun QueryResult.tick() {
        //TODO toItemStack not saving the prefab, breakpoint here
        val result = entity.encodeComponentsTo(type)
        recipes.wrapped.forEachIndexed { i, recipe ->
            @Suppress("DEPRECATION")
            recipe.register(NamespacedKey(prefabKey.plugin, "${prefabKey.name}$i"), result)
        }
        entity.remove<RegisterRecipeComponent>()
    }
}
