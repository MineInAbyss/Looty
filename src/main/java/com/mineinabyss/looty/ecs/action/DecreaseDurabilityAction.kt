package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.events.call
import com.mineinabyss.looty.dto.LootyEventNames
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent
import com.mineinabyss.looty.events.LootyItemBrokeEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:decrease_durability")
@AutoscanComponent
class DecreaseDurabilityAction(@SerialName("delta_durability") private val deltaDurability: Int) : GearyAction() {
    private val GearyEntity.durability by get<DurabilityComponent>()

    override fun GearyEntity.run(): Boolean {
        durability.durability -= deltaDurability

        get<MinDurabilityComponent>()?.let {
            durability.durability = durability.durability.coerceAtLeast(it.minDurability)
            if (durability.durability <= it.minDurability) {
                LootyItemBrokeEvent(this).call()
            }
        }

        return true
    }
}
