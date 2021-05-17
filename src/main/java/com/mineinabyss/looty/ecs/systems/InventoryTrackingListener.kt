package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.components.ItemComponent
import com.mineinabyss.geary.minecraft.hasComponentsEncoded
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.ecs.components.ChildItemCache
import com.mineinabyss.looty.ecs.components.PickedUpItemData
import com.mineinabyss.looty.ecs.components.inventory.SlotType
import com.mineinabyss.looty.looty
import com.okkero.skedule.schedule
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryInteractEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val player = whoClicked
        val gearyPlayer = geary(player)
        val pickedUpItemData = gearyPlayer.get<PickedUpItemData>()

        if (pickedUpItemData != null && cursor != null) {
            this.cursor = pickedUpItemData.item
            gearyPlayer.remove<PickedUpItemData>()
        }

        val cursor = cursor
        val currItem = currentItem

        val itemCache = gearyPlayer.get<ChildItemCache>() ?: return

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
        if (cursor == null || cursor.type == Material.AIR) {
            if (currItem != null) {
                itemCache[slot]?.encodeComponentsTo(currItem)
                // Creative inventory is basically all client-side, we cache the changes that need to be made
                // and apply them when the item is placed back.
                if (player.gameMode == GameMode.CREATIVE)
                    gearyPlayer.set(PickedUpItemData(currItem.clone()))
                else
                    currentItem = currItem
            }
            itemCache.remove(slot)
        }
        //otherwise, add cursor to cache
        else if (cursor.hasItemMeta() && clickedInventory == player.inventory) {
            //TODO re-reading meta here
            val meta = cursor.itemMeta
            if (!meta.persistentDataContainer.hasComponentsEncoded) return

            //clone required since item becomes AIR after this, I assume event messes with it
            val (entity, item) = LootyFactory.loadFromItem(geary(player), cursor.clone(), slot, addToInventory = false) ?: return
            if(slot == player.inventory.heldItemSlot)
                entity.add<SlotType.Held>()
        }
    }

    /** Immediately adds a held component to the currently held item. */
    //TODO another component for when in offhand
    //TODO remove held when swapping into offhand
    @EventHandler
    fun PlayerItemHeldEvent.onHeldItemSwap() {
        geary(player).with<ChildItemCache> { items ->
            items[previousSlot]?.remove<SlotType.Held>()
            items[newSlot]?.add<SlotType.Held>()
        }
    }

    //TODO is there a version safe way of getting this slot via enum or something?
    private const val offHandSlot = 40

    @EventHandler
    fun PlayerSwapHandItemsEvent.onSwapOffhand() {
        geary(player).with<ChildItemCache> { itemCache ->
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
        val gearyPlayer = geary(player)
        val itemCache = gearyPlayer.get<ChildItemCache>() ?: return

        val (slot, entity) = itemCache.itemMap.entries.firstOrNull { (slot, item) ->
            player.inventory.getItem(slot) != item.get<ItemComponent>()?.item
        } ?: return

        itemDrop.itemStack = itemDrop.itemStack.apply { entity.encodeComponentsTo(this) }
        itemCache.remove(slot)
//        itemCache[]?.encodeComponentsTo(itemDrop.itemStack)
    }

    @EventHandler
    fun EntityPickupItemEvent.onPickUpItem() {
        val player = entity as? Player ?: return
        if (item.itemStack.itemMeta.persistentDataContainer.hasComponentsEncoded)
            looty.schedule {
                waitFor(1)
//TODO                ItemTrackerSystem.refresh(geary(player))
            }
    }
}
