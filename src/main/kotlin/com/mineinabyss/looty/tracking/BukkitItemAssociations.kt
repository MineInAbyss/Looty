package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.papermc.GearyMCKoinComponent
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.store.decode
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.looty.ecs.components.PlayerSingletonItems
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftInventoryPlayer
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory
import java.util.*

/**
 * Returns a Geary entity if this item has a UUID encoded.
 *
 * Use [toGearyOrNull] to support player-instanced items.
 * Otherwise, this function specifically ignores them.
 */
fun ItemStack.toGearyFromUUIDOrNull(): GearyEntity? = GearyMCKoinComponent {
    if (!hasItemMeta()) return null
    val pdc = itemMeta.persistentDataContainer

    val uuid = pdc.decode<UUID>() ?: return null
    return uuid2entity[uuid]
}

/**
 * Returns a Geary entity if this item has a UUID encoded.
 *
 * Otherwise, returns the [player]-instanced entity based on the
 * first prefab encoded on this item.
 *
 * Use [toGearyFromUUIDOrNull] if you wish to ignore player-instanced items.
 */
suspend fun ItemStack.toGearyOrNull(player: Player): GearyEntity? = GearyMCKoinComponent {
    if (!hasItemMeta()) return null
    val pdc = itemMeta.persistentDataContainer

    // If a UUID is encoded, just return the item
    pdc.decode<UUID>()?.let { return uuid2entity[it] }

    // If no UUID, try to read as a player-instanced item
    val prefab = pdc.decodePrefabs().firstOrNull()
    return prefab?.let { player.toGeary().get<PlayerSingletonItems>()?.get(it) }
}

//TODO use idofront-nms
fun PlayerInventory.toNMS(): net.minecraft.world.entity.player.PlayerInventory =
    (this as CraftInventoryPlayer).inventory

fun ItemStack.toNMS(): net.minecraft.world.item.ItemStack =
    (this as CraftItemStack).handle

