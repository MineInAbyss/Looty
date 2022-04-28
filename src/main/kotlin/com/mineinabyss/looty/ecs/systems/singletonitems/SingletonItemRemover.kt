package com.mineinabyss.looty.ecs.systems.singletonitems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.geary.systems.TickingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.get
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.PlayerInstancedItems
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerSingletonContext

@AutoScan
class SingletonItemRemover : TickingSystem() {
    private val TargetScope.playerItems by get<PlayerInstancedItems>()

    override fun TargetScope.tick() {
        var foundHeld = false
        // Copy to avoid concurrency exception
        playerItems.toMap().forEach { (prefab, entity) ->
            entity.with { context: PlayerSingletonContext ->
                if (context.itemSlots.isEmpty()) {
                    playerItems.unload(prefab)
                    debug("Removed $prefab from player")
                    return@forEach
                }

                context.itemSlots.removeIf {
                    context.inventory.getItem(it)?.itemMeta?.persistentDataContainer?.decodePrefabs()
                        ?.firstOrNull() != prefab
                }

                if (!foundHeld && context.itemSlots.contains(context.inventory.heldItemSlot)) {
                    entity.add<SlotType.Held>()
                    foundHeld = true
                } else
                    entity.remove<SlotType.Held>()
            }
        }
    }
}
