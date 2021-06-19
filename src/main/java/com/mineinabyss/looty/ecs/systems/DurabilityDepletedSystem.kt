package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.BrokenComponent
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent

object DurabilityDepletedSystem : LootyItemSystem() {
    private val QueryResult.durabilityComponent by get<DurabilityComponent>()
    private val QueryResult.minDurabilityComponent by get<MinDurabilityComponent>()
    private val brokenComponent = lacks<BrokenComponent>()

    override fun QueryResult.tick() {
        if (durabilityComponent.durability <= minDurabilityComponent.minDurability) {
            entity.add<DurabilityDepletedComponent>()
        }
    }
}
