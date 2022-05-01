package com.mineinabyss.looty.ecs.components

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

/**
 * A component describing what the ItemStack for a looty item should look like
 */
@Serializable
@SerialName("looty:type")
class LootyType(
    val item: SerializableItemStack
) {
    fun createItem() = item.toItemStack()
    fun updateItem(applyTo: ItemStack) = item.toItemStack(applyTo)
}
