package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.minecraft.store.geary
import com.mineinabyss.geary.minecraft.store.get
import com.mineinabyss.geary.minecraft.store.isGearyEntity
import com.mineinabyss.geary.minecraft.store.with
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.idofront.destructure.component2
import com.mineinabyss.idofront.destructure.component3
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.systems.ItemTrackerSystem.updateAndSaveItems
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener {
    //TODO issues when clicking too fast causes duplicate item
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

        //shift clicking still considers cursor null and we don't know the location being put into TODO try to see if we can find it

        if (e.isShiftClick) return

        //TODO if both cursor and currItem aren't null, we try to swap them instead of removing
        //remove if cursor had nothing (item clicked on and taken out of inventory)
        if (cursor == null || cursor.type == Material.AIR)
            itemCache.remove(e.slot)
        //otherwise, add cursor to cache
        //TODO stop this if clicked inventory is chest as well
        else if (cursor.hasItemMeta() && e.clickedInventory != null) {
            //TODO re-reading meta here
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return
            itemCache.add(e.slot, cursor)
        }
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    @EventHandler
    fun onHeldItemSwap(e: PlayerItemHeldEvent) {
        val (player, prevSlot, newSlot) = e
        player.get<ChildItemCache>()?.swap(prevSlot, newSlot)
    }

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
                it.updateAndSaveItems(player.inventory)
            }
        }
    }

    @EventHandler
    fun onPickUpItem(e: EntityPickupItemEvent) {
        val player = e.entity as? Player ?: return
        geary(player) {
            with<ChildItemCache> {
                //TODO item is not in inventory yet when we run this, run next tick?
                it.updateAndSaveItems(player.inventory)
            }
        }
    }
}
