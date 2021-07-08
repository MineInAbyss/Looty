package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.geary.minecraft.access.geary
import com.mineinabyss.geary.minecraft.store.decode
import com.mineinabyss.geary.minecraft.store.decodePrefabs
import com.mineinabyss.looty.ecs.components.PlayerSingletonItems
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

fun gearyOrNull(
    item: ItemStack
): GearyEntity? {
    if (!item.hasItemMeta()) return null
    val pdc = item.itemMeta.persistentDataContainer

    val uuid = pdc.decode<UUID>() ?: return null
    return BukkitAssociations[uuid]
}

fun gearyOrNull(
    item: ItemStack,
    player: Player
): GearyEntity? {
    if (!item.hasItemMeta()) return null
    val pdc = item.itemMeta.persistentDataContainer

    val prefab = pdc.decodePrefabs().firstOrNull()
    return prefab?.let { geary(player).get<PlayerSingletonItems>()?.get(it) }
        ?: gearyOrNull(item)
}

fun PlayerInventory.toNMS() = (this as CraftInventoryPlayer).inventory

fun ItemStack.toNMS() = (this as CraftItemStack).handle

