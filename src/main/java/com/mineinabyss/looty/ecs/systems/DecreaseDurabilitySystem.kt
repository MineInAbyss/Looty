package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DecreaseDurabilityComponent
import com.mineinabyss.looty.ecs.components.DurabilityComponent

object DecreaseDurabilitySystem : LootyItemSystem() {
    private val decreaseDurabilityComponent by get<DecreaseDurabilityComponent>()
    private val durabilityComponent by get<DurabilityComponent>()

    override fun GearyEntity.tick() {
        durabilityComponent.durability =
            (durabilityComponent.durability - decreaseDurabilityComponent.deltaDurability).coerceAtLeast(0)
        remove<DecreaseDurabilityComponent>()
    }
}
