package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.IncreaseDurabilityComponent

object IncreaseDurabilitySystem : LootyItemSystem() {
    private val increaseDurabilityComponent by get<IncreaseDurabilityComponent>()
    private val durabilityComponent by get<DurabilityComponent>()

    override fun GearyEntity.tick() {
        durabilityComponent.durability += increaseDurabilityComponent.deltaDurability
        remove<IncreaseDurabilityComponent>()
    }
}
