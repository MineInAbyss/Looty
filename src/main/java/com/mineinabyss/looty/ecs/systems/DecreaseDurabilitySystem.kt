package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.CurrentDurabilityComponent
import com.mineinabyss.looty.ecs.components.DecreaseDurabilityComponent

object DecreaseDurabilitySystem : LootyItemSystem() {
    private val decrease by get<DecreaseDurabilityComponent>()
    private val durability by get<CurrentDurabilityComponent>()

    override fun GearyEntity.tick() {
        durability.currentDurability = (durability.currentDurability - decrease.delta).coerceAtLeast(0)
        remove<DecreaseDurabilityComponent>()
    }
}
