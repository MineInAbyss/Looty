package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.geary.minecraft.store.isGearyEntity
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.looty.ecs.components.ChildItemCache
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun itemMoveEvent(e: InventoryClickEvent) {
        val cursor = e.cursor //ADD: the item that was put into the slot
        val currItem = e.currentItem //REMOVE: the item that was clicked and its slot

        val player = e.whoClicked
        val itemCache = player.get<ChildItemCache>() ?: return

//        player.info("""
//            Cursor: ${e.cursor}
//            CurrentItem: ${e.currentItem}
//            Slot: ${e.slot}
//        """.trimIndent())

        if (currItem != null) {
            //if right clicking on an item with more than one stack, half of it will still
            // stay in the inv, so we shouldn't remove it
            if (e.click == ClickType.RIGHT && currItem.amount > 1) {
                //TODO update amount here as well
                return
            }
            //if adding onto a stack of existing items, just keep the old entity

            else if (cursor != null && e.click == ClickType.LEFT && currItem.isSimilar(cursor)) {
                itemCache.update(e.slot) {
                    //TODO the item in cache is sometimes the literal inventory item, causing duple glitches
                    // when we increase its amount
//                    amount = currItem.amount + cursor.amount
                }
                return
            }
        }

        //TODO we don't know what slot shift clicking puts the item into, but preferably
        // we'd like to move the entity's cached slot. Currently it just gets removed.
        //if(e.isShiftClick){}

        //remove if cursor had nothing (item clicked on and taken out of inventory)
        if (cursor == null || cursor.type == Material.AIR)
            itemCache.remove(e.slot)
        //otherwise, add cursor to cache
        else if (cursor.hasItemMeta() && e.clickedInventory == player.inventory) {
            //TODO re-reading meta here
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return
            //clone required since item becomes AIR after this, I assume event messes with it
            itemCache.add(e.slot, cursor.clone())
        }
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    /*@EventHandler
    fun onHeldItemSwap(e: PlayerItemHeldEvent) {
        val (player, prevSlot, newSlot) = e
        player.get<ChildItemCache>()?.swapHeld(prevSlot, newSlot)
    }*/

    //TODO is there a version safe way of getting this slot via enum or something?
    private const val offHandSlot = 40

    @EventHandler
    fun onSwapOffhand(e: PlayerSwapHandItemsEvent) {
        val (player) = e
        player.with<ChildItemCache> { itemCache ->
            val mainHandSlot = player.inventory.heldItemSlot

            itemCache.swap(mainHandSlot, offHandSlot)

            //we always want to remove from offhand and add into main hand
//            inventory.swapHeldComponent(removeFrom = offHandSlot, addTo = mainHandSlot)
        }

    }

    //TODO picking up and dropping items doesn't tell us which slot the item came/left from, so currently forces an
    // expensive re-read of inventory
    /**
     * There's no way of knowing which slot an item was in when dropped, so the most reliable way of ensuring nothing
     * funky happens is recalculating everything. Try and improve on this later!
     */
    @EventHandler
    fun onDropItem(e: PlayerDropItemEvent) {
        val (player) = e
        geary(player) {
            with<ChildItemCache> {
                it.update(player.inventory)
            }
        }
    }

    @EventHandler
    fun onPickUpItem(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        geary(player) {
            with<ChildItemCache> {
                //TODO item is not in inventory yet when we run this, run next tick?
                it.update(player.inventory)
            }
        }
    }
}
