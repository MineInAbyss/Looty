package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.ResultScope
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext

@AutoScan
class HeldItemTracker : TickingSystem() {
    init {
        has<SlotType.Held>()
    }

    private val ResultScope.context by get<PlayerInventoryContext>()

    override fun ResultScope.tick() {
        if (context.inventory.heldItemSlot != context.slot)
            entity.remove<SlotType.Held>()
    }
}
