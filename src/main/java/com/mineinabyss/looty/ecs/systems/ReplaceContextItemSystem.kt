package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.ReplaceContextItemComponent

object ReplaceContextItemSystem : LootyItemSystem() {
    private val QueryResult.updateContextItemComponent by get<ReplaceContextItemComponent>()
    private val QueryResult.context by get<PlayerInventoryContext>()

    override fun QueryResult.tick() {
        context.item?.let {
            val oldMeta = it.itemMeta
            val item = updateContextItemComponent.item.toItemStack().clone()
            item.itemMeta = oldMeta
            context.inventory.setItem(context.slot, item)
        }
        entity.remove<ReplaceContextItemComponent>()
    }
}
