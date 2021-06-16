package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.BrokenComponent
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent

object DurabilityDepletedSystem : LootyItemSystem() {
    private val durabilityComponent by get<DurabilityComponent>()
    private val minDurabilityComponent by get<MinDurabilityComponent>()
    private val brokenComponent = hasNot<BrokenComponent>()

    override fun GearyEntity.tick() {
        if (durabilityComponent.durability == minDurabilityComponent.minDurability) {
            add<DurabilityDepletedComponent>()
        }
    }
}
