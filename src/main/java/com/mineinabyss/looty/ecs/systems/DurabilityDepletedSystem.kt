package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.BrokenComponent
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent

object DurabilityDepletedSystem : LootyItemSystem() {
    private val durability by get<DurabilityComponent>()
    private val minDurability by get<MinDurabilityComponent>()

    override fun GearyEntity.tick() {
        if (!has<BrokenComponent>() && durability.durability == minDurability.minDurability) {
            add<DurabilityDepletedComponent>()
        }
    }
}
