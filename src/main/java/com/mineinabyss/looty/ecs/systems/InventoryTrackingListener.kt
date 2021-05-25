package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.minecraft.hasComponentsEncoded
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.looty
import com.mineinabyss.looty.tracking.gearyOrNull
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

object InventoryTrackingListener : Listener {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val cursor = cursor
        val currItem = currentItem ?: return
        val player = inventory.holder as? Player ?: return

        gearyOrNull(currItem)?.let { gearyItem ->
            PeriodicSaveSystem.saveToItem(
                entity = gearyItem,
                persisting = gearyItem.get() ?: return,
                item = currItem,
            )
            debug("Saved item ${currItem.type}")
        }

        if (cursor?.itemMeta?.persistentDataContainer?.hasComponentsEncoded == true) {
            LootyFactory.loadFromPlayerInventory(
                PlayerInventoryContext(
                    holder = player,
                    slot = slot,
                    inventory = player.inventory,
                ),
                item = cursor
            )
            debug("Loaded item ${cursor.type}")
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
        val gearyItem = gearyOrNull(item) ?: return
        PeriodicSaveSystem.saveToItem(
            entity = gearyItem,
            persisting = gearyItem.get() ?: return,
            item = item,
        )
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
