package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.inventory.SlotType

class HeldItemTracker : TickingSystem() {
    init {
        has<SlotType.Held>()
    }

    private val QueryResult.context by get<PlayerInventoryContext>()

    override fun QueryResult.tick() {
        if (context.inventory.heldItemSlot != context.slot)
            entity.remove<SlotType.Held>()
    }
}
