package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:decreaseDurability")
class DecreaseDurabilityAction(private val deltaDurability: Int) : GearyAction() {
    override fun GearyEntity.run(): Boolean {
        val durability = get<DurabilityComponent>() ?: return false
        durability.durability -= deltaDurability
        return true
    }
}
