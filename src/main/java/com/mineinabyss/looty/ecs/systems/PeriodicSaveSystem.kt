package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.get
import com.mineinabyss.geary.ecs.accessors.relation
import com.mineinabyss.geary.ecs.accessors.together
import com.mineinabyss.geary.ecs.api.autoscan.AutoScan
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.components.PersistingComponent
import com.mineinabyss.geary.minecraft.store.encode
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import org.bukkit.inventory.ItemStack

@AutoScan
class PeriodicSaveSystem : TickingSystem(interval = 100) {
    init {
        has<PlayerInventoryContext>()
    }
    private val TargetScope.persisting by relation<Any, PersistingComponent>().together()
    private val TargetScope.item by get<ItemStack>()

    override fun TargetScope.tick() {
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
