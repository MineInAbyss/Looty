package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:decrease_durability")
class DecreaseDurabilityAction(private val deltaDurability: Int) : GearyAction() {
    private val GearyEntity.durability by get<DurabilityComponent>()

    override fun GearyEntity.run(): Boolean {
        durability.durability -= deltaDurability
        return true
    }
}
