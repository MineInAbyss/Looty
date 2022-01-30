package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.papermc.GearyMCKoinComponent
import com.mineinabyss.geary.papermc.GearyScope
import com.mineinabyss.geary.papermc.hasComponentsEncoded
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.idofront.time.ticks
import com.mineinabyss.looty.LootyFactory
import com.mineinabyss.looty.debug
import com.mineinabyss.looty.ecs.components.itemcontexts.PlayerInventoryContext
import com.mineinabyss.looty.looty
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import com.okkero.skedule.BukkitDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent

object InventoryTrackingListener : Listener, GearyScope by GearyMCKoinComponent() {
    //TODO drag clicking is a separate event
    @EventHandler
    fun InventoryClickEvent.syncWithLooty() {
        val cursor = cursor
        val currItem = currentItem ?: return
        val player = inventory.holder as? Player ?: return

        runBlocking {
            currItem.toGearyFromUUIDOrNull()?.let { gearyItem ->
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
        runBlocking {
            gearyItem.encodeComponentsTo(item)
            gearyItem.removeEntity()
        }
    }

    //TODO
    @EventHandler
    fun EntityPickupItemEvent.onPickUpItem() {
        val player = entity as? Player ?: return
        if (item.itemStack.itemMeta.persistentDataContainer.hasComponentsEncoded)
            engine.launch(BukkitDispatcher(looty)) {
                delay(1.ticks)
                ItemTrackerSystem.refresh(player)
            }
    }
}
