package com.mineinabyss.looty.ecs.components.events.listeners

import com.mineinabyss.geary.ecs.api.entities.GearyEntity
import com.mineinabyss.looty.ecs.components.*
import java.util.*

fun replaceItemToBroken (entity: GearyEntity)
{
    if (entity.has<UUID>() && entity.has<LootyType>()) {
        val brokenItemComponent = entity.get<BrokenItemComponent>() ?: return
        val context = entity.get<PlayerInventoryContext>() ?: return

        context.item?.also {
            entity.set(ReplaceContextItemComponent(brokenItemComponent.item))
        }
    }
}
