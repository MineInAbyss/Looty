package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.minecraft.access.BukkitAssociations
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.UpdateContextItemComponent

object UpdateContextItemSystem : LootyItemSystem() {
    private val updateContextItemComponent by get<UpdateContextItemComponent>()

    override fun GearyEntity.tick() {
        val entity = BukkitAssociations[uuid] ?: return
        val context = entity.get<PlayerInventoryContext>()
        context?.item?.let {
            if (it.type != updateContextItemComponent.item.type) {
                it.setType(updateContextItemComponent.item.type)
                it.data = updateContextItemComponent.item.toItemStack().data
            }
            if (it.amount != updateContextItemComponent.item.amount) it.amount = updateContextItemComponent.item.amount
            context.inventory.setItem(context.slot, it)
        }
        remove<UpdateContextItemComponent>()
    }
}
