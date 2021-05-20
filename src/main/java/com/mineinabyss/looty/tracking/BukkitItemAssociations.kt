package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.geary.minecraft.store.decode
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

fun gearyOrNull(item: ItemStack): GearyEntity? {
    if(!item.hasItemMeta()) return null
    val uuid = item.itemMeta.persistentDataContainer.decode<UUID>() ?: return null
    return BukkitAssociations[uuid]
}

fun PlayerInventory.toNMS() = (this as CraftInventoryPlayer).inventory

fun ItemStack.toNMS() = (this as CraftItemStack).handle

