package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.has
import com.mineinabyss.geary.systems.TickingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.get
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventorySlotContext

@AutoScan
class HeldItemTracker : TickingSystem() {
    private val TargetScope.isHeld by family { has<SlotType.Held>() }
    private val TargetScope.context by get<PlayerInventorySlotContext>()

    override fun TargetScope.tick() {
        if (context.inventory.heldItemSlot != context.slot)
            entity.remove<SlotType.Held>()
    }
}
