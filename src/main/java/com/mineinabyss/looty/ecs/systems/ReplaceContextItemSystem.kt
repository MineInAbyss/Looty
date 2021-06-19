package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.ItemReplacementComponent

object ReplaceContextItemSystem : LootyItemSystem() {
    private val updateContextItemComponent by get<ItemReplacementComponent>()
    private val context by get<PlayerInventoryContext>()

    override fun GearyEntity.tick() {
        context.item?.let {
            val oldMeta = it.itemMeta
            val item = updateContextItemComponent.item.toItemStack().clone()
            item.itemMeta = oldMeta
            context.inventory.setItem(context.slot, item)
        }
        remove<ItemReplacementComponent>()
    }
}
