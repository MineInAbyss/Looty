package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.RegisterRecipeComponent
import com.mineinabyss.looty.toItemStack
import org.bukkit.NamespacedKey

class ItemRecipeSystem : TickingSystem() {
    val recipes by get<RegisterRecipeComponent>()
    val type by get<LootyType>()
    val prefabKey by get<PrefabKey>()

    override fun GearyEntity.tick() {
        //TODO toItemStack not saving the prefab, breakpoint here
        val result = type.toItemStack(this, encodePrefabKey = true)
        recipes.wrapped.forEachIndexed { i, recipe ->
            @Suppress("DEPRECATION")
            recipe.register(NamespacedKey(prefabKey.plugin, "${prefabKey.name}$i"), result)
        }
        remove<RegisterRecipeComponent>()
    }
}
