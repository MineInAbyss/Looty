package com.mineinabyss.looty.ecs.components

import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

/**
 * A component describing what the ItemStack for a looty item should look like
 */
@Serializable
@SerialName("looty:type")
class LootyType(
    val item: SerializableItemStack
) {
    fun createItem() = item.toItemStack()
    fun createItem(applyTo: ItemStack) = item.toItemStack(applyTo)
    //FIXME actually implement correct logic here
    fun updateItem(applyTo: ItemMeta) = item.updateMeta(ItemStack(Material.STONE), applyTo)
}
