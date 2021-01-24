package com.mineinabyss.looty.config

import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.geary.minecraft.store.encodeComponentsTo
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LootyType(
    val item: SerializableItemStack
) : GearyEntityType() {
    @Transient
    override val types = LootyTypes

    fun instantiateItemStack() = item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
}
