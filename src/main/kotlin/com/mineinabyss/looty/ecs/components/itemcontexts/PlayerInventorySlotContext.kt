package com.mineinabyss.looty.ecs.components.itemcontexts

import org.bukkit.entity.Player
import org.bukkit.inventory.PlayerInventory

data class PlayerInventorySlotContext(
    val holder: Player,
    val slot: Int,
    val inventory: PlayerInventory = holder.inventory
) {
//    val item get() = inventory.getItem(slot)

    fun removeItem() {
        inventory.setItem(slot, null)
    }
}
