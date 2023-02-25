package com.mineinabyss.looty.features.wearables

import com.mineinabyss.geary.helpers.with
import com.mineinabyss.geary.papermc.components.Hat
import com.mineinabyss.geary.papermc.store.hasComponentsEncoded
import com.mineinabyss.geary.papermc.tracking.items.getGearyItem
import com.mineinabyss.idofront.entities.rightClicked
import com.mineinabyss.looty.tracking.toGearyFromUUIDOrNull
import com.mineinabyss.looty.tracking.toGearyOrNull
import org.bukkit.Sound
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractAtEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot

class WearableItemSystem : Listener {
    @EventHandler
    fun InventoryClickEvent.shiftClickToWear() {
        if (!click.isShiftClick) return
        if (clickedInventory !== inventory) return // Only support shift clicking when only player inventory is open
        val player = inventory.holder as? Player ?: return
        player.getGearyItem(slot)?.with { _: Hat ->
            if (player.inventory.helmet == null) {
                player.inventory.helmet = currentItem
                currentItem = null
                isCancelled = true
            }
        }
    }

    @EventHandler
    fun InventoryClickEvent.clickToWear() {
        if (slotType !== InventoryType.SlotType.ARMOR) return
        if (rawSlot != 5) return
        if (inventory.holder !is Player) return

        val cursor = cursor ?: return

        if (cursor.itemMeta?.persistentDataContainer?.hasComponentsEncoded == false) return

        val entity = cursor.toGearyFromUUIDOrNull()
        entity?.get<Hat>() ?: return

        val currItem = currentItem?.clone() ?: return // item will not be null, it will be air

        // swap the items from cursor to helmet slot
        currentItem = cursor.clone()
        view.cursor = currItem
        isCancelled = true
    }

    @EventHandler
    fun PlayerInteractEvent.rightClickToWear() {
        if (hand == EquipmentSlot.OFF_HAND) return //the event is called twice, on for each hand. We want to ignore the offhand call
        if (!rightClicked) return //only do stuff when player rightclicks
        if (player.inventory.helmet !== null) return // don't equip if we are wearing a helmet

        val currItem = player.inventory.itemInMainHand
        if (currItem.itemMeta?.persistentDataContainer?.hasComponentsEncoded == false) return

        val entity = currItem.toGearyOrNull(player)
        val hat = entity?.get<Hat>() ?: return // item is not a hat

        player.inventory.helmet = currItem.clone()
        player.playSound(player.location, hat.sound, 1f, 1f)
        currItem.subtract(1)
    }

    @EventHandler
    fun PlayerInteractAtEntityEvent.rightClickArmorStand() {
        val item = player.inventory.itemInMainHand
        val armorstand = rightClicked as? ArmorStand ?: return

        if (hand != EquipmentSlot.HAND) return
        if (rightClicked.type != EntityType.ARMOR_STAND) return
        item.toGearyOrNull(player)?.get<Hat>() ?: return

        armorstand.equipment.helmet = item
        item.subtract(1)
        player.playSound(rightClicked.location, Sound.ITEM_ARMOR_EQUIP_NETHERITE, 1f, 1f)

    }
}
