package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.SerializableItemStack
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bukkit.inventory.ItemStack

@Serializable
@SerialName("looty:change_item_visual")
@AutoscanComponent
class ChangeItemVisualAction(val item: SerializableItemStack) : GearyAction() {
    private val GearyEntity.originalItem by get<ItemStack>()

    override fun GearyEntity.run(): Boolean {
        val oldMeta = originalItem.itemMeta
        item.toItemStack(originalItem)
        originalItem.itemMeta = oldMeta
        return true
    }
}
