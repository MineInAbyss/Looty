package com.mineinabyss.looty.ecs.systems.singletonitems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.minecraft.store.decodePrefabs
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.PlayerSingletonItems
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerSingletonContext

object SingletonItemRemover : TickingSystem() {
    private val QueryResult.playerItems by get<PlayerSingletonItems>()

    override fun QueryResult.tick() {
        var foundHeld = false
        playerItems.forEach { (prefab, entity) ->
            entity.with<PlayerSingletonContext> { context ->
                context.itemSlots.removeIf {
                    context.inventory.getItem(it)?.itemMeta?.persistentDataContainer?.decodePrefabs()?.first() != prefab
                }

                when {
                    context.itemSlots.isEmpty() -> {
                        playerItems.unload(prefab)
                        debug("Removed $prefab from player")
                    }
                    context.itemSlots.contains(context.inventory.heldItemSlot) -> entity.add<SlotType.Held>()
                    else -> entity.remove<SlotType.Held>()
                }
            }
        }
    }
}
