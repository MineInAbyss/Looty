package com.mineinabyss.looty.ecs.components

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A component describing what the ItemStack for a looty item should look like
 */
@Serializable
@SerialName("looty:type")
class LootyType(val item: SerializableItemStack)
