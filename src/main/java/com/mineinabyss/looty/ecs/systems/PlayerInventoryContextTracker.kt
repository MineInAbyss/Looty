package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.store.decode
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import java.util.*

@AutoScan
class PlayerInventoryContextTracker : TickingSystem() {
    private val TargetScope.context by get<PlayerInventoryContext>()
    private val TargetScope.uuid by get<UUID>()

    override fun TargetScope.tick() {
        val item = context.inventory.getItem(context.slot)
        //TODO more efficient decoding via NMS
        if (item == null || item.itemMeta.persistentDataContainer.decode<UUID>() != uuid) {
            entity.removeEntity()
            debug("Removed ${item?.type} from slot ${context.slot}")
            return
        }
        if (context.inventory.heldItemSlot == context.slot)
            entity.add<SlotType.Held>()

        entity.set(item)
    }
}
