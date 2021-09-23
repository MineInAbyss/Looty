package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.minecraft.hasComponentsEncoded
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.Hat
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import com.mineinabyss.looty.looty
import com.mineinabyss.looty.tracking.gearyOrNull
import com.okkero.skedule.schedule
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val cursor = cursor
        val currItem = currentItem ?: return
        val player = inventory.holder as? Player ?: return

        gearyOrNull(currItem)?.let { gearyItem ->
            gearyItem.encodeComponentsTo(currItem)
            debug("Saved item ${currItem.type}")
        }

        if (cursor?.itemMeta?.persistentDataContainer?.hasComponentsEncoded == true) {
            LootyFactory.loadFromPlayerInventory(
                PlayerInventoryContext(
                    holder = player,
                    slot = slot,
                ),
                item = cursor
            )
            debug("Loaded item ${cursor.type}")
        }
    }

    @EventHandler
    fun InventoryClickEvent.wearable() {
        if (slotType !== InventoryType.SlotType.ARMOR) return
        if (rawSlot != 5) return

        val cursor = cursor ?: return
        val player = inventory.holder as? Player ?: return

        if (!cursor.itemMeta.persistentDataContainer.hasComponentsEncoded) return

        val entity = LootyFactory.loadFromPlayerInventory(
            PlayerInventoryContext(
                holder = player,
                slot = slot,
            ),
            item = cursor
        ) ?: return

        entity.get<Hat>() ?: return

        val currItem = currentItem?.clone() ?: return // item will not be null, it will be air

        // swap the items from cursor to helmet slot
        currentItem = cursor.clone()
        this.cursor = currItem
        isCancelled = true
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
        val gearyItem = gearyOrNull(item) ?: return
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
