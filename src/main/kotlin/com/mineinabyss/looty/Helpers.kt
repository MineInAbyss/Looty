package com.mineinabyss.looty

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.papermc.GearyMCContext
import com.mineinabyss.geary.papermc.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.messaging.broadcast
import com.mineinabyss.looty.config.LootyConfig
import com.mineinabyss.looty.ecs.components.LootyType
import org.bukkit.inventory.ItemStack

internal fun debug(message: Any?) {
    if (LootyConfig.data.debug) broadcast(message)
}

context(GearyMCContext)
fun GearyEntity.encodeComponentsTo(lootyType: LootyType): ItemStack =
    lootyType.createItem().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
