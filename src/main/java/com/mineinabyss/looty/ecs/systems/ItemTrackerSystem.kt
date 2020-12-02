@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.geary.minecraft.store.*
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.Held
import com.mineinabyss.looty.ecs.components.LootyEntity
import org.bukkit.inventory.PlayerInventory

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
    override fun tick() = Engine.forEach<PlayerComponent, ChildItemCache> { (player), childItemCache ->
        //TODO make children use an engine too, then easily remove all held components
        childItemCache.updateAndSaveItems(player.inventory)

        //Add a held component to currently held item
        childItemCache[player.inventory.heldItemSlot]?.addComponent(Held())
    }

    //TODO If an entity is ever not removed properly from ECS but is removed from the cache, it will forever exist but
    // not be tracked. Either we need a GC or make 1000% this never fails.
    fun ChildItemCache.updateAndSaveItems(inventory: PlayerInventory) {
        val heldSlot = inventory.heldItemSlot
        //TODO prevent issues with children and id changes

        inventory.forEachIndexed { slot, item ->

            //================================ TODO MOVE OUT PROLLY cause we re-read meta when adding entity
            if (item == null || !item.hasItemMeta()) return@forEachIndexed
            val meta = item.itemMeta
            val container = meta.persistentDataContainer
            if (!container.isGearyEntity) return@forEachIndexed //TODO perhaps some way of knowing this without cloning the ItemMeta
            //================================

            val cachedItemEntity: LootyEntity? = get(slot)

            //if the items match exactly, encode components to the itemstack
            if (item == cachedItemEntity?.item)
                cachedItemEntity.writeToItem(item)
            //otherwise try to find an equivalent item to attach back to (i.e. it was moved but we didn't notice)
            // or finally add this as a new item to the system
            else {
                //TODO separate adding components into separate system?
                val equivalent: LootyEntity? = TODO("Find equivalent")
                if (equivalent != null)
                //TODO I don't like moving items around all willy nilly, if an error has occurred we should just
                // forget about it and re-serialize, not have a chance to attach to an unrelated item that happens to equal.
                    move(equivalent.slot, slot)
                //if we didn't find an equal item, this must be a new one
                else add(slot, item)
            }
            //TODO managing whether an item is in main hand/offhand/armor, etc...
            // This might be better to just evaluate as we go if we know slot in LootyEntity

        }

        //TODO call killCache on the item cache or the like here to remove all items that were overridden but not reassigned
    }
}
