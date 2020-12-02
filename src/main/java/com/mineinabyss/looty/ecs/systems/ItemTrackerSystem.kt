@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.*
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.forEach
import com.mineinabyss.geary.ecs.systems.TickingSystem
import com.mineinabyss.geary.minecraft.components.PlayerComponent
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.Held

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
        childItemCache.update(player.inventory)

        //Add a held component to currently held item
        childItemCache[player.inventory.heldItemSlot]?.addComponent(Held())
    }
}
