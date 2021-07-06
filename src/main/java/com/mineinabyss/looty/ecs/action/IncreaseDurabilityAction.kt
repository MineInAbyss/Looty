package com.mineinabyss.looty.ecs.action

import com.mineinabyss.geary.ecs.api.actions.GearyAction
import com.mineinabyss.geary.ecs.api.autoscan.AutoscanComponent
import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.events.call
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.MaxDurabilityComponent
import com.mineinabyss.looty.ecs.components.MinDurabilityComponent
import com.mineinabyss.looty.events.LootyItemDurabilityChangedEvent
import com.mineinabyss.looty.events.LootyItemRepairedEvent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("looty:increase_durability")
@AutoscanComponent
class IncreaseDurabilityAction(@SerialName("delta_durability") private val deltaDurability: Int) : GearyAction() {
    private val GearyEntity.durability by get<DurabilityComponent>()

    override fun GearyEntity.run(): Boolean {
        val oldDurability = durability.durability
        durability.durability += deltaDurability

        get<MaxDurabilityComponent>()?.let {
            durability.durability = durability.durability.coerceAtMost(it.maxDurability)
        }

        LootyItemDurabilityChangedEvent(this).call()

        get<MinDurabilityComponent>()?.let {
            if (oldDurability <= it.minDurability && durability.durability > it.minDurability) {
                LootyItemRepairedEvent(this).call()
            }
        }

        return true
    }
}
