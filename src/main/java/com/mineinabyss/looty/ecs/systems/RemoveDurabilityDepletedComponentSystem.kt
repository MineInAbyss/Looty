package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.BrokenComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent

object RemoveDurabilityDepletedComponentSystem : LootyItemSystem() {
    private val durabilityDepleteComponent = has<DurabilityDepletedComponent>()

    override fun QueryResult.tick() {
        entity.remove<DurabilityDepletedComponent>()
        entity.setPersisting(BrokenComponent())
    }
}
