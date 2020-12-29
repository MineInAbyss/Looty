package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.addComponent
import com.mineinabyss.geary.ecs.components.removeComponent
import com.mineinabyss.geary.minecraft.isGearyEntity
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.itemMoveEvent() {
        val cursor = cursor
        val currItem = currentItem

        val player = whoClicked
        val itemCache = player.get<ChildItemCache>() ?: return

//        player.info("""
//            Cursor: ${e.cursor}
//            CurrentItem: ${e.currentItem}
//            Slot: ${e.slot}
//        """.trimIndent())

        if (currItem != null) {
            //if right clicking on an item with more than one stack, half of it will still
            // stay in the inv, so we shouldn't remove it
            if (click == ClickType.RIGHT && currItem.amount > 1) {
                //TODO update amount here as well
                return
            }
            //if adding onto a stack of existing items, just keep the old entity

            else if (cursor != null && click == ClickType.LEFT && currItem.isSimilar(cursor)) {
                itemCache.update(slot) {
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
            itemCache.remove(slot)
        //otherwise, add cursor to cache
        else if (cursor.hasItemMeta() && clickedInventory == player.inventory) {
            //TODO re-reading meta here
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return
            //clone required since item becomes AIR after this, I assume event messes with it
            itemCache.add(slot, cursor.clone())
        }
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    @EventHandler
    fun PlayerItemHeldEvent.onHeldItemSwap() {
        player.with<ChildItemCache> { items ->
            items[previousSlot]?.removeComponent<SlotType.Held>()
            items[newSlot]?.addComponent(SlotType.Held)
        }
    }

    //TODO is there a version safe way of getting this slot via enum or something?
    private const val offHandSlot = 40

    @EventHandler
    fun PlayerSwapHandItemsEvent.onSwapOffhand() {
        player.with<ChildItemCache> { itemCache ->
            val mainHandSlot = player.inventory.heldItemSlot

            itemCache.swap(mainHandSlot, offHandSlot)

            itemCache[mainHandSlot]?.apply {
                addComponent(SlotType.Held)
                removeComponent<SlotType.Offhand>()
            }

            itemCache[offHandSlot]?.apply {
                addComponent(SlotType.Offhand)
                removeComponent<SlotType.Held>()
            }
        }
    }

    //TODO picking up and dropping items doesn't tell us which slot the item came/left from, so currently forces an
    // expensive re-read of inventory
    /**
     * There's no way of knowing which slot an item was in when dropped, so the most reliable way of ensuring nothing
     * funky happens is recalculating everything. Try and improve on this later!
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun PlayerDropItemEvent.onDropItem() {
        player.with<ChildItemCache> {
            it.reevaluate(player.inventory)
        }
    }

    /*@EventHandler
    fun EntityPickupItemEvent.onPickUpItem() {
        val player = entity as? Player ?: return
        player.with<ChildItemCache> {
            //TODO item is not in inventory yet when we run this, run next tick?
            it.reevaluate(player.inventory)
        }
    }*/
}
