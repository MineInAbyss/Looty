package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.ecs.serialization.Formats
import com.mineinabyss.geary.minecraft.components.of
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.geary.minecraft.store.toComponentKey
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.InitByPrefab
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.looty

object InitByPrefabSystem : TickingSystem() {
    private val context by get<PlayerInventoryContext>()
    private val initByPrefab by get<InitByPrefab>()

    override fun GearyEntity.tick() {
        val prefabType = initByPrefab.prefabTypeName
        val prefab = PrefabManager[PrefabKey.of(looty, prefabType)] ?: return;
        var lootyType = prefab.get<LootyType>() ?: return
        val contextItem = context.item ?: return
        val key = Formats.getSerialNameFor(InitByPrefab::class)?.toComponentKey() ?: return
        remove<InitByPrefab>()
        addPrefab(prefab)
        set(lootyType)
        encodeComponentsTo(contextItem)
        contextItem.editItemMeta {
            persistentDataContainer.remove(key)
        }
    }
}
