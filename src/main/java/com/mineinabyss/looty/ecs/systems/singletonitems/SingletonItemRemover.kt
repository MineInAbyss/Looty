package com.mineinabyss.looty.ecs.systems.singletonitems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.entities.with
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.store.decodePrefabs
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.PlayerSingletonItems
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerSingletonContext

@AutoScan
class SingletonItemRemover : TickingSystem() {
    private val TargetScope.playerItems by get<PlayerSingletonItems>()

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
