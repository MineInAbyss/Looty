@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.autoscan.AutoScan
import com.mineinabyss.geary.ecs.accessors.TargetScope
import com.mineinabyss.geary.ecs.accessors.building.get
import com.mineinabyss.geary.ecs.api.annotations.Handler
import com.mineinabyss.geary.ecs.api.systems.GearyListener
import com.mineinabyss.geary.ecs.api.systems.TickingSystem
import com.mineinabyss.geary.ecs.events.EntityRemoved
import com.mineinabyss.geary.papermc.hasComponentsEncoded
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

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
@AutoScan
object ItemTrackerSystem : TickingSystem(interval = 5.seconds) {
    private val TargetScope.player by get<Player>()

    override suspend fun TargetScope.tick() {
        refresh(player)
    }

    //TODO If an entity is ever not removed properly from ECS but is removed from the cache, it will forever exist but
    // not be tracked. Either we need a GC or make 1000% this never fails.
    suspend fun refresh(player: Player) {
        player.inventory.forEachIndexed { slot, item ->
            if (item != null && item.hasItemMeta() && item.itemMeta.persistentDataContainer.hasComponentsEncoded)
                LootyFactory.loadFromPlayerInventory(PlayerInventoryContext(player, slot))
        }

        //TODO held item
    }

    @AutoScan
    private class TrackOnLogin : GearyListener() {
        val TargetScope.player by added<Player>()

        @Handler
        suspend fun TargetScope.track() {
            ItemTrackerSystem.refresh(player)
        }
    }

    @AutoScan
    private class UntrackOnLogout : GearyListener() {
        val TargetScope.player by get<Player>()

        override suspend fun onStart() {
            event.has<EntityRemoved>()
        }

        @Handler
        suspend fun TargetScope.logout() {
            ItemTrackerSystem.refresh(player)
        }
    }
}
