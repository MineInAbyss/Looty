package com.mineinabyss.looty.ecs.components

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:type")
class LootyType(
    val item: SerializableItemStack
) {
    fun createItem() = item.toItemStack()
}
