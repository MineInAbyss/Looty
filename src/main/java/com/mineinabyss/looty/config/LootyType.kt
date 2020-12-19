package com.mineinabyss.looty.config

import com.mineinabyss.geary.ecs.GearyComponent
import com.mineinabyss.geary.ecs.types.GearyEntityType
import com.mineinabyss.idofront.items.editItemMeta
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LootyType(
        val item: SerializableItemStack,
        private val _name: String? = null,
        private val _staticComponents: MutableSet<GearyComponent> = mutableSetOf(),
        private val _components: Set<GearyComponent> = setOf(),
) : GearyEntityType() {
    @Transient
    override val types = LootyTypes

    fun instantiateItemStack() = item.toItemStack().editItemMeta {
        encodeComponentsTo(persistentDataContainer)
    }
}
