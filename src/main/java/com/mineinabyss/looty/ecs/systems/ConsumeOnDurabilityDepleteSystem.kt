package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.ConsumeOnDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext

object ConsumeOnDurabilityDepleteSystem : LootyItemSystem() {
    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()
    private val context by get<PlayerInventoryContext>()
    private val a = has<ConsumeOnDurabilityDepleteComponent>(set = true)

    override fun GearyEntity.tick() {
        context.item?.apply { context.inventory.remove(this) }
    }
}
