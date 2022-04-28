package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.datatypes.family.family
import com.mineinabyss.geary.components.PersistingComponent
import com.mineinabyss.geary.datatypes.family.MutableFamilyOperations.Companion.has
import com.mineinabyss.geary.papermc.store.encode
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.geary.systems.TickingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.building.flatten
import com.mineinabyss.geary.systems.accessors.get
import com.mineinabyss.geary.systems.accessors.relation
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventorySlotContext
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

@AutoScan
class PeriodicSaveSystem : TickingSystem(interval = 5.seconds) {
    private val TargetScope.persisting by relation<Any, PersistingComponent>().flatten()
    private val TargetScope.item by get<ItemStack>()
    private val TargetScope.inInventory by family { has<PlayerInventorySlotContext>() }

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
