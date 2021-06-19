package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.entities.addPrefab
import com.mineinabyss.geary.ecs.prefab.PrefabKey
import com.mineinabyss.geary.ecs.prefab.PrefabManager
import com.mineinabyss.geary.minecraft.components.of
import com.mineinabyss.looty.ecs.components.Init
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.encodeComponentsTo
import com.mineinabyss.looty.looty

object InitByPrefabSystem : TickingSystem() {
    private val context by get<PlayerInventoryContext>()
    private val init by get<Init>()

    override fun GearyEntity.tick() {
        val type = init.prefabTypeName
        val prefab = PrefabManager[PrefabKey.of(looty, type)] ?: return;
        var lootyType = prefab.get<LootyType>() ?: return
        addPrefab(prefab)
        set(lootyType)
        remove<Init>()
        val item = encodeComponentsTo(lootyType)
        set(item)
        context.inventory.setItem(context.slot, item)
    }
}
