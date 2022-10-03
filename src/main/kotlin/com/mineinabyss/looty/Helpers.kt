package com.mineinabyss.looty

import com.mineinabyss.geary.datatypes.GearyEntity
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.lootyConfig
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

internal fun debug(message: Any?) {
    if (lootyConfig.debug) broadcast(message)
}

fun GearyEntity.encodeComponentsTo(lootyType: LootyType): ItemStack =
    lootyType.item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
