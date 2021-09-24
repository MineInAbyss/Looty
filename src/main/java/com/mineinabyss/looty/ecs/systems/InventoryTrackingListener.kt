package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.minecraft.hasComponentsEncoded
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.entities.rightClicked
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
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot

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
    fun InventoryClickEvent.shiftClick() {
        val player = inventory.holder as? Player ?: return

        val entity = LootyFactory.loadFromPlayerInventory(
            PlayerInventoryContext(
                holder = player,
                slot = slot,
            ),
            item = currentItem
        ) ?: return
        entity.get<Hat>() ?: return

        if (player.inventory.helmet == null && click.isShiftClick) {

            player.inventory.helmet = currentItem
            currentItem = null
            isCancelled = true
        }
    }

    @EventHandler
    fun InventoryClickEvent.wearable() {
        if (slotType !== InventoryType.SlotType.ARMOR) return
        if (rawSlot != 5) return

        val cursor = cursor ?: return
        val player = inventory.holder as? Player ?: return

        if (cursor.itemMeta?.persistentDataContainer?.hasComponentsEncoded == false) return

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
        view.cursor = currItem
        isCancelled = true
    }

    @EventHandler
    fun PlayerInteractEvent.equipWearable() {
        if (hand == EquipmentSlot.OFF_HAND) return //the event is called twice, on for each hand. We want to ignore the offhand call
        if (!rightClicked) return //only do stuff when player rightclicks
        if (player.inventory.helmet !== null) return // don't equip if we are wearing a helmet

        val currItem = player.inventory.itemInMainHand
        if (currItem.itemMeta?.persistentDataContainer?.hasComponentsEncoded == false) return

        val entity = LootyFactory.loadFromPlayerInventory(
            PlayerInventoryContext(
                holder = player,
                slot = player.inventory.heldItemSlot,
            ),
            item = currItem
        ) ?: return // item is not from looty

        val hat = entity.get<Hat>() ?: return // item is not a hat

        player.inventory.helmet = currItem.clone()
        player.playSound(player.location, hat.sound, 1f, 1f)
        currItem.subtract(1)
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
