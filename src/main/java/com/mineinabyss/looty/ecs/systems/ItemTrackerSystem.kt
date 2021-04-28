@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.ItemComponent
import com.mineinabyss.geary.minecraft.hasComponentsEncoded
import com.mineinabyss.looty.addLooty
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.inventory.SlotType
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
    val player = has<Player>()
    val itemCache by get<ChildItemCache>()

    override fun GearyEntity.tick() {
        lootyRefresh()
    }
}

//TODO If an entity is ever not removed properly from ECS but is removed from the cache, it will forever exist but
// not be tracked. Either we need a GC or make 1000% this never fails.
@Synchronized
fun GearyEntity.lootyRefresh() {
    val player = get<Player>() ?: return
    val itemCache = get<ChildItemCache>() ?: return

    //we remove any items from this copy that were modified, whatever remains will be removed
    val untouched = itemCache.itemMap
    //TODO prevent issues with children and id changes

    player.inventory.forEachIndexed { slot, item ->
        //================================ TODO MOVE OUT PROLLY cause we re-read meta when adding entity
        if (item == null || !item.hasItemMeta()) return@forEachIndexed
        val meta = item.itemMeta
        val container = meta.persistentDataContainer
        if (!container.hasComponentsEncoded) return@forEachIndexed //TODO perhaps some way of knowing this without cloning the ItemMeta
        //================================

        val originalItem = itemCache[slot]?.get<ItemComponent>()

        //FIXME if changes were made to the ECS entity, they should be re-serialized here
        // currently the changes on the actual entity will just be ignored
        //if the items don't match, add the new item to this slot
        if (item != originalItem?.item)
            addLooty(item, slot)

        untouched -= slot
    }

    untouched.keys.forEach { itemCache.remove(it) }

    itemCache[player.inventory.heldItemSlot]?.add<SlotType.Held>()
}
