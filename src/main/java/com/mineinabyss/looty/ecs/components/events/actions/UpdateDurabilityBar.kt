package com.mineinabyss.looty.ecs.components.events.actions

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.idofront.items.damage
import com.mineinabyss.looty.ecs.components.DurabilityComponent
import com.mineinabyss.looty.ecs.components.MaxDurabilityComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext

fun GearyEntity.updateDurabilityBar()
{
    get<PlayerInventoryContext>()?.apply {
        val durability = get<DurabilityComponent>()?.durability ?: return
        val maxDurability = get<MaxDurabilityComponent>()?.maxDurability ?: return
        item?.also {
            var newDurability = it.type.maxDurability.let { it - it * durability / maxDurability }
            val itemMeta = it.itemMeta
            itemMeta.damage = when {
                newDurability < 0 -> 0
                newDurability < 1 -> 1
                else -> newDurability
            }
            it.itemMeta = itemMeta
        }
    }
}


