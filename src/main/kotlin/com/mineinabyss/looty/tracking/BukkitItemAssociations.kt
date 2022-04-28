package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.papermc.store.decode
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.looty.ecs.components.PlayerInstancedItems
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

/**
 * Returns a Geary entity if this item has a UUID encoded.
 *
 * Use [toGearyOrNull] to support player-instanced items.
 * Otherwise, this function specifically ignores them.
 */
fun ItemStack.toGearyFromUUIDOrNull(): GearyEntity? {
    if (!hasItemMeta()) return null
    return itemMeta.toGearyFromUUIDOrNull()
}

fun ItemMeta.toGearyFromUUIDOrNull(): GearyEntity? {
    val uuid = persistentDataContainer.decode<UUID>() ?: return null
    return globalContextMC.uuid2entity[uuid]
}

/**
 * Returns a Geary entity if this item has a UUID encoded.
 *
 * Otherwise, returns the [player]-instanced entity based on the
 * first prefab encoded on this item.
 *
 * Use [toGearyFromUUIDOrNull] if you wish to ignore player-instanced items.
 */
fun ItemStack.toGearyOrNull(player: Player): GearyEntity? {
    if (!hasItemMeta()) return null
    val pdc = itemMeta.persistentDataContainer

    // If a UUID is encoded, just return the item
    pdc.decode<UUID>()?.let { return globalContextMC.uuid2entity[it] }

    // If no UUID, try to read as a player-instanced item
    val prefab = pdc.decodePrefabs().firstOrNull()
    return prefab?.let { player.toGeary().get<PlayerInstancedItems>()?.get(it) }
}
