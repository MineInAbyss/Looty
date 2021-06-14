package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.BrokenComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent

object RemoveDurabilityDepletedComponentSystem : LootyItemSystem() {
    private val durabilityDepleteComponent = has<DurabilityDepletedComponent>()

    override fun GearyEntity.tick() {
        remove<DurabilityDepletedComponent>()
        setPersisting(BrokenComponent())
    }
}
