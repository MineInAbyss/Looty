package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.IncreaseDurabilityComponent

object IncreaseDurabilitySystem : LootyItemSystem() {
    private val decrease by get<IncreaseDurabilityComponent>()
    private val durability by get<DurabilityComponent>()

    override fun GearyEntity.tick() {
        durability.durability -= decrease.deltaDurability
        remove<IncreaseDurabilityComponent>()
    }
}
