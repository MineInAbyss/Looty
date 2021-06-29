package com.mineinabyss.looty.ecs.components.events.actions

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.action.ChangeItemVisualAction
import com.mineinabyss.looty.ecs.components.*

fun GearyEntity.updateDurabilityItem()
{
    get<PlayerInventoryContext>()?.also {
        val durability = get<DurabilityComponent>()?.durability ?: return
        val maxDurability = get<MaxDurabilityComponent>()?.maxDurability ?: return
        val percent = (durability.toFloat() / maxDurability * 100).toInt()
        get<DurabilityState>()?.let {
            val state = it.states
                .firstOrNull { state -> percent in state.min..state.max }
            when(state) {
                null -> get<LootyType>()?.item
                else -> state.item
            }?.also { item -> ChangeItemVisualAction(item).runOn(this) }
        }
    }
}


