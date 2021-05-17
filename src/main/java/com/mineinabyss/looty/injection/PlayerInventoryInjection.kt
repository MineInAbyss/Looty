package com.mineinabyss.looty.injection

import com.google.common.collect.ImmutableList
import net.minecraft.server.v1_16_R3.ItemStack
import net.minecraft.server.v1_16_R3.NonNullList
import net.minecraft.server.v1_16_R3.PlayerInventory
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent

private const val INVENTORY_ROWS_FIELD = "f"

class PlayerInventoryInjection : Listener {
    @EventHandler
    fun PlayerLoginEvent.injectInventoryOnLogin() {
        val nmsInv = (player.inventory as CraftInventoryPlayer).inventory

        try {
            val rows = listOf("items", "armor", "extraSlots").map { fieldName ->
                val field = PlayerInventory::class.java.getDeclaredField(fieldName)
                field.isAccessible = true
                @Suppress("UNCHECKED_CAST")
                val currentList = field.get(nmsInv) as NonNullList<ItemStack>

                val wrappedList = TrackingNonNullList(currentList, player)

                field.set(nmsInv, wrappedList)
                wrappedList
            }
            val field = PlayerInventory::class.java.getDeclaredField(INVENTORY_ROWS_FIELD)
            field.isAccessible = true
            field.set(nmsInv, rows)
        } catch (reason: Throwable) {
            reason.printStackTrace()
            error("Looty failed to inject inventory tracking")
        }
    }
}
