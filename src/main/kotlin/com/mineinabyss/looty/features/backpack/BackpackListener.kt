package com.mineinabyss.looty.features.backpack

import com.mineinabyss.geary.papermc.tracking.entities.toGearyOrNull
import com.mineinabyss.geary.papermc.tracking.items.toGeary
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class BackpackListener : Listener {

    private fun isBackpack(player: Player, slot: EquipmentSlot) = getBackpack(player, slot) != null
    private fun getBackpack(player: Player, slot: EquipmentSlot) = player.inventory.toGeary()?.get(slot)

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerInteractEvent.onBackpackOpen() {
        val (item, hand) = (item ?: return) to (hand ?: return)
        val backpack = getBackpack(player, hand) ?: return

        val title = if (item.itemMeta.hasDisplayName()) item.itemMeta.displayName()!! else item.displayName()
        val content = backpack.get<BackpackContents>()?.contents
        val inventory = Bukkit.createInventory(player, InventoryType.CHEST, title)
        content?.let { inventory.contents = it.toTypedArray() }
        player.openInventory(inventory)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun InventoryCloseEvent.onCloseBackpack() {
        val player = player as Player
        val backpack = getBackpack(player, EquipmentSlot.HAND) ?: getBackpack(player, EquipmentSlot.OFF_HAND) ?: return
        backpack.setPersisting(BackpackContents(inventory.contents.toList().map { it ?: ItemStack(Material.AIR) }))
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerDropItemEvent.onDropBackpack() {
        if (itemDrop.toGearyOrNull()?.has<Backpack>() == true) player.closeInventory()
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun PlayerSwapHandItemsEvent.onSwapBackpack() {
        if (isBackpack(player, EquipmentSlot.HAND) || isBackpack(player, EquipmentSlot.OFF_HAND)) player.closeInventory()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun PlayerDeathEvent.onDeath() = player.closeInventory()

    @EventHandler
    fun BlockPlaceEvent.onPlaceBackpack() {
        if (isBackpack(player, EquipmentSlot.HAND) || isBackpack(player, EquipmentSlot.OFF_HAND)) isCancelled = true
    }
}
