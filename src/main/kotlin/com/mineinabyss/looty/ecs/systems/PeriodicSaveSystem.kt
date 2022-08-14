package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.components.relations.Persists
import com.mineinabyss.geary.papermc.store.encode
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.geary.systems.accessors.building.flatten
import com.mineinabyss.idofront.items.editItemMeta
import org.bukkit.inventory.ItemStack
import kotlin.time.Duration.Companion.seconds

@AutoScan
class PeriodicSaveSystem : RepeatingSystem(interval = 5.seconds) {
    private val TargetScope.persisting by getRelations<Persists, Any>().flatten()
    private val TargetScope.item by get<ItemStack>()

    override fun TargetScope.tick() {
        val forceSave = every(iterations = 100)

        if (forceSave) {
            entity.encodeComponentsTo(item)
            return
        }

        item.editItemMeta {
            persisting.forEach {
                val newHash = it.targetData.hashCode()
                if (newHash != it.data.hash) {
                    it.data.hash = newHash
                    persistentDataContainer.encode(it.targetData)
                }
            }
        }
    }
}
