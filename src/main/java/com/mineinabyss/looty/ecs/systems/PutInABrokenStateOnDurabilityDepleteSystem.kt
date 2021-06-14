package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.PutInABrokenStateDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.UpdateContextItemComponent

object PutInABrokenStateOnDurabilityDepleteSystem : LootyItemSystem() {
    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()
    private val putInABrokenStateDurabilityDepleteComponent by get<PutInABrokenStateDurabilityDepleteComponent>()
    private val context by get<PlayerInventoryContext>()

    override fun GearyEntity.tick() {
        context.item?.let {
            if (!lootyType.item.type.isItem) return
            set(UpdateContextItemComponent(putInABrokenStateDurabilityDepleteComponent.item))
        }
    }
}
