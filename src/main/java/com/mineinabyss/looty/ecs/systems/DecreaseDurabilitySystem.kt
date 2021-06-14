package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DecreaseDurabilityComponent
import com.mineinabyss.looty.ecs.components.DurabilityComponent

object DecreaseDurabilitySystem : LootyItemSystem() {
    private val decrease by get<DecreaseDurabilityComponent>()
    private val durability by get<DurabilityComponent>()

    override fun GearyEntity.tick() {
        durability.durability = (durability.durability - decrease.deltaDurability).coerceAtLeast(0)
        remove<DecreaseDurabilityComponent>()
    }
}
