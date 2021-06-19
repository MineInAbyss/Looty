package com.mineinabyss.looty.ecs.systems

import com.mineinabyss.geary.ecs.engine.QueryResult
import com.mineinabyss.looty.ecs.components.DurabilityDepletedComponent
import com.mineinabyss.looty.ecs.components.PlayerInventoryContext
import com.mineinabyss.looty.ecs.components.BrokenItemComponent
import com.mineinabyss.looty.ecs.components.ReplaceContextItemComponent

object ReplaceItemToBrokenOnDurabilityDepleteSystem : LootyItemSystem() {
    private val QueryResult.brokenItemComponent by get<BrokenItemComponent>()
    private val QueryResult.context by get<PlayerInventoryContext>()

    private val durabilityDepletedComponent = has<DurabilityDepletedComponent>()

    override fun QueryResult.tick() {
        context.item?.also {
            if (!lootyType.item.type.isItem) return
            entity.set(ReplaceContextItemComponent(brokenItemComponent.item))
        }
    }
}
