package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.flatten
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.accessors.building.relation
import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PersistingComponent
import com.mineinabyss.geary.papermc.store.encode
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

@AutoScan
class PeriodicSaveSystem : TickingSystem(interval = 5.seconds) {
    override suspend fun onStart() {
        has<PlayerInventoryContext>()
    }

    private val TargetScope.persisting by relation<Any, PersistingComponent>().flatten()
    private val TargetScope.item by get<ItemStack>()

    override suspend fun TargetScope.tick() {
        val forceSave = every(iterations = 100)

        if (forceSave) {
            entity.encodeComponentsTo(item)
            return
        }

        item.editItemMeta {
            persisting
            persisting.forEach { (key, persistingInfo) ->
                val newHash = key.hashCode()
                if (newHash != persistingInfo.hash) {
                    persistingInfo.hash = newHash
                    persistentDataContainer.encode(key)
                }
            }
        }
    }
}
