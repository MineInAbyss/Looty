package com.mineinabyss.looty.ecs.components.events.listeners

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.ConsumeOnDurabilityDepleteComponent
import com.mineinabyss.looty.ecs.components.LootyType
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import java.util.*

fun consumeItemOnBroke(entity: GearyEntity)
{
    if (
        entity.has<ConsumeOnDurabilityDepleteComponent>()
        && entity.has<UUID>()
        && entity.has<LootyType>()
    ) {
        val context = entity.get<PlayerInventoryContext>() ?: return
        context.item?.apply { context.inventory.remove(this) }
    }
}
