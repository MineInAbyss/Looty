package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

class BukkitItemAssociations {
}

fun gearyOrNull(item: ItemStack): GearyEntity? = gearyOrNull(item.toNMS())

fun gearyOrNull(item: net.minecraft.server.v1_16_R3.ItemStack): GearyEntity? {
    return BukkitAssociations.getOrNull(item.lootyUUID ?: return null)
}

fun PlayerInventory.toNMS() = (this as CraftInventoryPlayer).inventory

fun ItemStack.toNMS() = (this as CraftItemStack).handle

var net.minecraft.server.v1_16_R3.ItemStack.lootyUUID: UUID?
    get() = tag.getUUID("looty:uuid")
    set(value) = tag.setUUID("looty:uuid", value)
