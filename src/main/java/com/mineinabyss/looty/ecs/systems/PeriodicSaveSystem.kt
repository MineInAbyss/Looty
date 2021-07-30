package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PersistingComponent
import com.mineinabyss.geary.ecs.engine.iteration.QueryResult
import com.mineinabyss.geary.minecraft.store.encode
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import org.bukkit.inventory.ItemStack

object PeriodicSaveSystem : TickingSystem(interval = 100) {
    init {
        has<PlayerInventoryContext>()
    }
    private val QueryResult.persisting by allRelationsWithData<PersistingComponent>()
    private val QueryResult.item by get<ItemStack>()

    override fun QueryResult.tick() {
        val forceSave = every(iterations = 100)

        if (forceSave) {
            entity.encodeComponentsTo(item)
            return
        }

        item.editItemMeta {
            persisting.forEach { (persistingComponentInfo, componentData) ->
                val newHash = componentData.hashCode()
                if (newHash != persistingComponentInfo.hash) {
                    persistingComponentInfo.hash = newHash
                    persistentDataContainer.encode(componentData)
                }
            }
        }
    }
}
