package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext

@AutoScan
class HeldItemTracker : TickingSystem() {
    override suspend fun onStart() {
        has<SlotType.Held>()
    }

    private val TargetScope.context by get<PlayerInventoryContext>()

    override suspend fun TargetScope.tick() {
        if (context.inventory.heldItemSlot != context.slot)
            entity.remove<SlotType.Held>()
    }
}
