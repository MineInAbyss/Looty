package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.geary.papermc.store.hasComponentsEncoded
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventorySlotContext
import com.mineinabyss.looty.ecs.components.itemcontexts.useWithLooty
import com.mineinabyss.looty.loadItem
import com.mineinabyss.looty.looty
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import com.okkero.skedule.schedule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.PlayerInventory

context(GearyMCContext)
object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val cursor = cursor
        val currItem = currentItem ?: return
        val inventory = inventory as? PlayerInventory ?: return
        val player = inventory.holder as Player

        currItem.toGearyFromUUIDOrNull()?.let { gearyItem ->
            gearyItem.encodeComponentsTo(currItem)
            debug("Saved item ${currItem.type}")
        }


        cursor?.useWithLooty {
            PlayerInventorySlotContext(player, slot, inventory).loadItem()
        }
    }

    //TODO
    @EventHandler
    fun PlayerSwapHandItemsEvent.onSwapOffhand() {
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
        }*/
    }

    //TODO
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
            looty.schedule {
                waitFor(1)
                ItemTrackerSystem.refresh(player)
            }
    }
}
