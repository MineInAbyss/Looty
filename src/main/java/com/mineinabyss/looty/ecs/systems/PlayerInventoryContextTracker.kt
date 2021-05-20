package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.store.decode
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import java.util.*

class PlayerInventoryContextTracker : TickingSystem() {
    private val context by get<PlayerInventoryContext>()
    private val uuid by get<UUID>()

    override fun GearyEntity.tick() {
        val item = context.inventory.getItem(context.slot)
        //TODO more efficient decoding via NMS
        if (item == null || item.itemMeta.persistentDataContainer.decode<UUID>() != uuid) {
            removeEntity()
            return
        }
        set(item)
    }
}
