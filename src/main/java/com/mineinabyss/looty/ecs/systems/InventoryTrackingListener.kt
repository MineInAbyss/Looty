package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.components.addChild
import com.mineinabyss.geary.ecs.components.addComponents
import com.mineinabyss.geary.ecs.components.with
import com.mineinabyss.geary.ecs.engine.Engine
import com.mineinabyss.geary.ecs.engine.entity
import com.mineinabyss.geary.minecraft.store.*
import com.mineinabyss.idofront.destructure.component1
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.LootyEntity
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
        val inventory = player.get<ChildItemCache>() ?: return

//        player.info("""
//            Cursor: ${e.cursor}
//            CurrentItem: ${e.currentItem}
//            Slot: ${e.slot}
//        """.trimIndent())

        //shift clicking still considers cursor null and we don't know the location being put into TODO try to see if we can find it
        if (e.isShiftClick) return

        //TODO if both cursor and currItem aren't null, we try to swap them instead of removing
        if (cursor == null || cursor.type == Material.AIR) { //remove if cursor had nothing
            inventory.remove(e.slot)
        } else if (cursor.hasItemMeta()) { //otherwise, cursor into cache
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.isGearyEntity) return

            val entity = Engine.entity {
                addComponents(meta.persistentDataContainer.decodeComponents())
            }
            //TODO adding child should be done within inv
            geary(player)?.addChild(entity)
            inventory[e.slot] = LootyEntity(entity.gearyId, cursor)
        }
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    @EventHandler
    fun onHeldItemSwap(e: PlayerItemHeldEvent) {
//        val (player, prevSlot, newSlot) = e //TODO switch to this when updating idofront
        val player = e.player
        val prevSlot = e.previousSlot
        val newSlot = e.newSlot
        player.get<ChildItemCache>()?.swapHeldComponent(prevSlot, newSlot)
    }

    private const val offHandSlot = 40 //TODO is there a version safe way of getting this slot via enum or something?

    @EventHandler
    fun onSwapOffhand(e: PlayerSwapHandItemsEvent) {
        val (player) = e
        player.with<ChildItemCache> { inventory ->
            val mainHandSlot = player.inventory.heldItemSlot

            inventory.swap(mainHandSlot, offHandSlot)

            //we always want to remove from offhand and add into main hand
            inventory.swapHeldComponent(removeFrom = offHandSlot, addTo = mainHandSlot)
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
