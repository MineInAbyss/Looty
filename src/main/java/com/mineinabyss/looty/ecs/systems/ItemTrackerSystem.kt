@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.geary.minecraft.store.GearyStore
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.tracking.gearyOrNull
import com.mineinabyss.looty.tracking.lootyUUID
import com.mineinabyss.looty.tracking.toNMS
import org.bukkit.entity.Player

/**
 * ItemStack instances are super disposable, they don't represent real items. Additionally, tracking items is
 * very inconsistent, so we must cache all components from an item, then periodically check to ensure these items
 * are still there, alongside all the item movement events available to us.
 *
 * ## Process:
 * - An Inventory component stores a cache of items, which we read and compare to actual items in the inventory.
 * - We go through geary items in the inventory and ensure the right items match our existing slots.
 * - If an item is a mismatch, we add it to a list of mismatches
 * - If an item isn't in our cache, we check the mismatches or deserialize it into the cache.
 * - All valid items get re-serialized TODO in the future there should be some form of dirty tag so we aren't unnecessarily serializing things
 */
object ItemTrackerSystem : TickingSystem(interval = 100) {
    val player by get<Player>()
    val itemCache by get<ChildItemCache>()

    override fun GearyEntity.tick() {
        refresh(this, player, itemCache)
    }

    //TODO If an entity is ever not removed properly from ECS but is removed from the cache, it will forever exist but
    // not be tracked. Either we need a GC or make 1000% this never fails.
    @Synchronized
    fun refresh(entity: GearyEntity, player: Player, itemCache: ChildItemCache) {
        //we remove any items from this copy that were modified, whatever remains will be removed
        val untouched = itemCache.itemMap
        //TODO prevent issues with children and id changes

        val nmsInv = player.inventory.toNMS()

        nmsInv.contents.forEachIndexed { slot, item ->
            val uuid = item.lootyUUID ?: return@forEachIndexed
            BukkitAssociations.getOrNull(uuid)
            GearyStore.decode(uuid)
            gearyOrNull(item)?.apply {

            }
        }

        untouched.keys.forEach { itemCache.remove(it) }

        itemCache[player.inventory.heldItemSlot]?.add<SlotType.Held>()

    }
}
