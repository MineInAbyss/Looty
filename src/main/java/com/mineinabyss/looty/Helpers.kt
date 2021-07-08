package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}

fun GearyEntity.encodeComponentsTo(lootyType: LootyType): ItemStack =
    lootyType.item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
