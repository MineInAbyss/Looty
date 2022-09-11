package com.mineinabyss.looty.tracking

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.access.toGeary
import com.mineinabyss.geary.papermc.globalContextMC
import com.mineinabyss.geary.papermc.store.decode
import com.mineinabyss.geary.papermc.store.decodePrefabs
import com.mineinabyss.idofront.nms.nbt.fastPDC
import com.mineinabyss.looty.ecs.components.PlayerItemCache
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataContainer
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
    return persistentDataContainer.toGearyFromUUIDOrNull()
}

fun PersistentDataContainer.toGearyFromUUIDOrNull(): GearyEntity? {
    val uuid = decode<UUID>() ?: return null
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
    val pdc = fastPDC ?: return null

    // If a UUID is encoded, just return the item
    pdc.decode<UUID>()?.let { return globalContextMC.uuid2entity[it] }

    // If no UUID, try to read as a player-instanced item
    val prefab = pdc.decodePrefabs().firstOrNull()?.toEntityOrNull() ?: return null
    return player.toGeary().get<PlayerItemCache>()?.getInstance(prefab)
}
