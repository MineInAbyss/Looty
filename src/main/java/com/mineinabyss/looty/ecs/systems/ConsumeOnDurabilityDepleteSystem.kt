package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.ConsumeOnDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext

object ConsumeOnDurabilityDepleteSystem : LootyItemSystem() {
    private val QueryResult.context by get<PlayerInventoryContext>()
    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()
    private val consumeOnDurabilityDepleteComponent = has<ConsumeOnDurabilityDepleteComponent>(set = true)

    override fun QueryResult.tick() {
        context.item?.apply { context.inventory.remove(this) }
    }
}
