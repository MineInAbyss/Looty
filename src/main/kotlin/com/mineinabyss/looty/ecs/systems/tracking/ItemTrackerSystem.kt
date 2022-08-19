@file:Suppress("UNREACHABLE_CODE")

package com.mineinabyss.looty.ecs.systems.tracking

import com.mineinabyss.geary.annotations.AutoScan
import com.mineinabyss.geary.annotations.Handler
import com.mineinabyss.geary.datatypes.forEachBit
import com.mineinabyss.geary.datatypes.pop1
import com.mineinabyss.geary.datatypes.setBit
import com.mineinabyss.geary.helpers.toGeary
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.systems.GearyListener
import com.mineinabyss.geary.systems.RepeatingSystem
import com.mineinabyss.geary.systems.accessors.TargetScope
import com.mineinabyss.idofront.nms.aliases.NMSItemStack
import com.mineinabyss.idofront.nms.aliases.toNMS
import com.mineinabyss.idofront.nms.nbt.fastPDC
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.LootyFactory.ItemState.*
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.PlayerItemCache
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap
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
@AutoScan
class ItemTrackerSystem : RepeatingSystem(interval = 1.ticks) {
    private val TargetScope.player by get<Player>()
    private val TargetScope.itemCache by get<PlayerItemCache>()

    override fun TargetScope.tick() {
        refresh(player, itemCache)
    }


    @AutoScan
    class TrackOnLogin : GearyListener() {
        val TargetScope.player by onSet<Player>()

        @Handler
        fun TargetScope.track() {
            entity.set(PlayerItemCache(entity))
        }
    }

    companion object : GearyMCContext by GearyMCContextKoin() {
        // Avoids bukkit items since ItemMeta does a lot of copying which adds overhead
        fun refresh(player: Player, cache: PlayerItemCache) {
            val nmsInv = player.toNMS().inventory
            var slot = 0

            // Map of removed items looking for a possible new position to their slots
            val toRemove = Long2LongOpenHashMap()
            // Set of new items looking for a possible old item to fill their heart :)
//            val toMatch = mutableSetOf<Loaded>()
            val toMatch = Array<Loaded?>(64) { null }
            val toAdd = mutableSetOf<NotLoaded>()

            fun attemptMove(loaded: Loaded): Boolean {
                val id = loaded.entity.id.toLong()
                if (id !in toRemove) return false
                val oldSlots = toRemove[id]
                val popped = oldSlots.pop1()
                val oldSlot = (oldSlots xor popped).countTrailingZeroBits()
                // If old slot is looking for the entity in our current slot
                val oldSlotLoading = toMatch[oldSlot]
                if (oldSlotLoading?.entity == cache[loaded.slot]) {
                    cache.swap(oldSlot, loaded.slot)
                    toMatch[oldSlot] = null
                } else cache.move(oldSlot, loaded.slot)
                if (popped == 0L) toRemove.remove(id)
                toRemove[id] = popped
                return true
            }

            fun calculateForItem(item: NMSItemStack, slot: Int) {
//                val currItem = cache.getItem(slot)
//                if (currItem == item && item != ItemStack.EMPTY) return
                val pdc = item.fastPDC
                // Get uuid or prefab entity
                val itemState = LootyFactory.getItemState(pdc, slot, item)
                val currEntity = cache[slot]
                // Based on them, check whether the item has changed
                when (itemState) {
                    is Empty -> {}
                    is Loaded ->
                        if (currEntity != itemState.entity) toMatch[slot] = itemState
                        else cache.updateItem(slot, item) // Update ItemStack component to always lead to an up-to-date reference
                    is NotLoaded -> toAdd += itemState
                }
                if (currEntity != itemState.entity) {
                    val currId = currEntity.id.toLong()
                    if (currId != 0L) toRemove[currId] = toRemove[currId].setBit(slot)
                }
            }

            nmsInv.compartments.forEach { comp ->
                comp.forEach itemLoop@{ item ->
                    calculateForItem(item, slot++)
                }
            }

            // Consider cursor item as last slot
            calculateForItem(player.toNMS().containerMenu.carried, 63)

            // Try to match any changes with removed items, otherwise load them and update cache
            for (loaded in toMatch) {
                if (loaded == null) continue
                if (!attemptMove(loaded))
                    cache[loaded.slot] = LootyFactory.loadItem(player.toGeary(), loaded.pdc)
            }

            // remove anything left in toRemove
            toRemove.forEach { (entityId, slot) ->
                val entity = entityId.toGeary()
                // If no other entity replaced a slot, we remove it now
                slot.forEachBit {
                    if (cache[it] == entity && cache.remove(it, true))
                    //TODO display entity name
                        debug("Removed $entity from ${player.name}")
                }
            }

            // Add queued up items
            toAdd.forEach {
                cache[it.slot] = LootyFactory.loadItem(player.toGeary(), it.pdc)
            }

            // Set held item
//            if (context.inventory.heldItemSlot == context.slot)
//                entity.add<SlotType.Held>()
        }
    }
}
