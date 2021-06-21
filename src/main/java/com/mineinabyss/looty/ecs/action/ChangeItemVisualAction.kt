package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.serialization.SerializableItemStack
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:change_item_visual")
@AutoscanComponent
class ChangeItemVisualAction(val item: SerializableItemStack) : GearyAction() {
    private val GearyEntity.context by get<PlayerInventoryContext>()

    override fun GearyEntity.run(): Boolean {
        val oldMeta = context.item?.itemMeta ?: return false
        val newItem = item.toItemStack().clone()
        newItem.itemMeta = oldMeta
        context.inventory.setItem(context.slot, newItem)
        return true
    }
}
