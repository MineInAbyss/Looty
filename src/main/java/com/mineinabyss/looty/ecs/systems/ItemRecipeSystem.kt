package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.GearyType
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.minecraft.store.encodeComponents
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.Init
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.RegisterRecipeComponent
import org.bukkit.NamespacedKey

class ItemRecipeSystem : TickingSystem() {
    private val recipes by get<RegisterRecipeComponent>()
    private val lootyType by get<LootyType>()
    private val prefabKey by get<PrefabKey>()

    override fun GearyEntity.tick() {
        remove<RegisterRecipeComponent>()
        val result = lootyType.item.toItemStack().editItemMeta {
            val components = List(1) { Init(prefabKey.name) }
            persistentDataContainer.encodeComponents(components, GearyType())
        }
        recipes.wrapped.forEachIndexed { i, recipe ->
            @Suppress("DEPRECATION")
            recipe.register(NamespacedKey(prefabKey.plugin, "${prefabKey.name}$i"), result)
        }
    }
}
