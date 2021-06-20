package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.geary.minecraft.store.decode
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import java.util.*

class HeldItemTracker : TickingSystem() {
    private val QueryResult.context by get<PlayerInventoryContext>()
    private val held = has<SlotType.Held>(set = false)

    override fun QueryResult.tick() {
        if (context.inventory.heldItemSlot != context.slot)
            entity.remove<SlotType.Held>()
    }
}
