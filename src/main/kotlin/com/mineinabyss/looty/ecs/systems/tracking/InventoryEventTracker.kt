/*
package com.mineinabyss.looty.ecs.systems.tracking

import com.github.shynixn.mccoroutine.bukkit.launch
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.GearyMCContextKoin
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.geary.papermc.store.hasComponentsEncoded
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.itemcontexts.ItemLocation
import com.mineinabyss.looty.ecs.helpers.useWithLooty
import com.mineinabyss.looty.loadItem
import com.mineinabyss.looty.looty
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import kotlinx.coroutines.delay
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.PlayerInventory

*/
/**
 * Keeps inventory in sync with geary entities by listening to related events.
 *//*

object InventoryEventTracker : Listener, GearyMCContext by GearyMCContextKoin() {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val cursor = cursor
        val currItem = currentItem
        val inventory = clickedInventory as? PlayerInventory ?: return
        val player = inventory.holder as Player

        currItem?.toGearyFromUUIDOrNull()?.let { gearyItem ->
            gearyItem.encodeComponentsTo(currItem)
            debug("Saved item ${currItem.type}")
        }

        cursor?.useWithLooty {
            loadItem(ItemLocation(player, slot, inventory))
        }
    }

    //TODO
    @EventHandler
    fun PlayerSwapHandItemsEvent.onSwapOffhand() {
        */
/*geary(player).with<ChildItemCache> { itemCache ->
            val mainHandSlot = player.inventory.heldItemSlot

            itemCache.swap(mainHandSlot, offHandSlot)

            itemCache[mainHandSlot]?.apply {
                add<SlotType.Held>()
                remove<SlotType.Offhand>()
            }

            itemCache[offHandSlot]?.apply {
                add<SlotType.Offhand>()
                remove<SlotType.Held>()
            }
        }*//*

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun PlayerDropItemEvent.onDropItem() {
        val item = itemDrop.itemStack
        val gearyItem = item.toGearyFromUUIDOrNull() ?: return
        gearyItem.encodeComponentsTo(item)
        gearyItem.removeEntity()
    }

    //TODO
    @EventHandler
    fun EntityPickupItemEvent.onPickUpItem() {
        val player = entity as? Player ?: return
        if (item.itemStack.itemMeta.persistentDataContainer.hasComponentsEncoded)
            looty.launch {
                delay(1.ticks)
                ItemTrackerSystem.refresh(player)
            }
    }
}
*/
